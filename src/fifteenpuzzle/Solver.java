package fifteenpuzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class Solver {
	private static int size;
	private static int[][] goal_state;
	public static void main(String[] args) throws IOException {
		System.out.println("number of arguments: " + args.length);
		//int[][] b=createBoard("board1.txt");
	
		if (args.length < 2) {
			System.out.println("File names are not specified");
			System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
			return;
		}
		
		List<String> sol;
		System.out.println("The Solution is the direction of the null space");
		int[][] initial=null;
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			initial=createBoard(args[i]+".txt");
			createSol();
			i++;
			sol=solve(initial);
			//System.out.println(args[i]);
			File f;
			FileWriter fw;
			f = new File(args[i]+".txt");
			fw = new FileWriter(f);
			fw.write("The Solution is the direction of the null space");
			for(String s:sol) {
				System.out.println(s);
				fw.write(s+"\n");
			}
			fw.close();
		}
		//File input = new File(args[0]);
		// solve...
		//File output = new File(args[1]);
		
	}
	
	

    private static final int[][] MOVES = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private static class State implements Comparable<State> {
        int[][] board;
        int cost;
        int moves;
        State prev;

        public State(int[][] board, int cost, int moves, State prev) {
            this.board = board;
            this.cost = cost;
            this.moves = moves;
            this.prev = prev;
        }

        public int compareTo(State other) {
            return (cost + heuristic()) - (other.cost + other.heuristic());
        }

        public boolean equals(Object other) {
            if (other instanceof State) {
                return Arrays.deepEquals(board, ((State) other).board);
            }
            return false;
        }

        public int hashCode() {
            return Arrays.deepHashCode(board);
        }

        private int heuristic() {
            int count = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] != goal_state[i][j]) {
                        int value = board[i][j];
                        int row = (value - 1) / size;
                        int col = (value - 1) % size;
                        count += Math.abs(row - i) + Math.abs(col - j);
                    }
                }
            }
            return count;
        }
        
    }

    private static int findBlankRow(int[][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int findBlankCol(int[][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return j;
                }
            }
        }
        return -1;
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    private static void swapTiles(int[][] board, int row1, int col1, int row2, int col2) {
        int temp = board[row1][col1];
        board[row1][col1] = board[row2][col2];
        board[row2][col2] = temp;
    }

    public static List<String> solve(int[][] initialBoard) {
        PriorityQueue<State> queue = new PriorityQueue<>();
        Set<State> visited = new HashSet<State>();

        State initial = new State(initialBoard, 0, 0, null);
        queue.offer(initial);

        while (!queue.isEmpty()) {
            State curr = queue.poll();
            if (Arrays.deepEquals(curr.board, goal_state)) {
                List<String> moves = new ArrayList<>();
                while (curr.prev != null) {
                    int blankRow = findBlankRow(curr.board);
                    int blankCol = findBlankCol(curr.board);
                    State prev = curr.prev;
                    int prevRow = findBlankRow(prev.board);
                    int prevCol = findBlankCol(prev.board);
                    if (prevRow == blankRow) {
                        if (prevCol > blankCol) {
                            moves.add("L");
                        } else {
                            moves.add("R");
                        }
                    } else {
                        if (prevRow > blankRow) {
                            moves.add("U");
                        } else {
                            moves.add("D");
                        }
                    }
                    curr = prev;
                }
                Collections.reverse(moves);
                return moves;
            }
            visited.add(curr);

            int blankRow = findBlankRow(curr.board);
            int blankCol = findBlankCol(curr.board);

            for (int[] move : MOVES) {
                int newRow = blankRow + move[0];
                int newCol = blankCol + move[1];

                if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                    int[][] newBoard = copyBoard(curr.board);
                    swapTiles(newBoard, blankRow, blankCol, newRow, newCol);

                    State next = new State(newBoard, curr.cost + 1, curr.moves+1, curr);

                    if (!visited.contains(next)) {
                        queue.offer(next);
                    }
                }
            }
            
        }
        System.out.println("no solution found");
        return null;
    }
    
    
    /*public static void main(String[] args) {
    	 
    	int[][] initial= {
    		{1,2,3,4},
    		{5,6,7,8},
    		{9,10,0,12},
   			{13,14,11,15}
    	};
    	List<String> solved = solve(initial);
    	for(int a=0;a<solved.size();a++) {
    		System.out.println(solved.get(a));
    	}
    	
    }*/
    
    public static int[][] createBoard(String fileName) throws FileNotFoundException {
    	File file;
    	
    	try {
			file = new File(fileName);
		} catch(Exception e) {
			throw new FileNotFoundException("File Not Found");
		}
		Scanner reader = new Scanner(file);
		String s = reader.nextLine();
		size=Integer.parseInt(s);
		s = reader.nextLine();
		String[][] board=new String[size][size];
    	String[] row=new String[size];
		//check for spaces if there's two spaces in a row add one the the
		//board if only one space add none
		int j=0;
		while(j<size){
			int x=0,i=0;
			row=s.split("");
			while(i<row.length-2) {
				//if first element in row add to board
				if(i==0) {
					board[x][j]=row[i]+row[i+1];
					//System.out.print(board[x][j]+" ");
					x++;
					i=i+2;
				//if two spaces in a row add next element
				} else if(row[i+1].equals(" ")&&row[i].equals(" ")) {
					board[x][j]=" "+row[i+2];
					//System.out.print(row[i]+" ");
					i+=3;
					x++;
				//if above conditions are false and current element is a space
				//add the next two elements
				} else if(row[i].equals(" ")) {
					board[x][j]=row[i+1]+row[i+2];
					//System.out.print(row[i]+" ");
					i+=3;
					x++;
				}
			}
			//checking board
			if(reader.hasNext())
				s=reader.nextLine();
			//System.out.println();
			j++;
		}
		reader.close();
		int[][] b = StringtoInt(board);
		board=null;
		return b;
    }
    
    private static int[][] StringtoInt(String[][] s){
    	int x=0,y=0;
    	int[][] board=new int[size][size];
    	for(String[] a: s) {
    		x=0;
    		for(String b: a) {
    			if(b.equals("  ")) {
    				board[x][y]=0;
    			}else {
    				try {
    				board[x][y]=Integer.parseInt(b.split(" ")[1]);
    				}catch(Exception e) {
    					board[x][y]=Integer.parseInt(b);
    				}
    			}
    			x++;
    		}
    		y++;
    	}
    	toPrint(board);
    	s=null;
    	return board;
    	
    }
   
   private static void toPrint(int[][] board) {
	   System.out.println("board");
	   for(int[] a: board) {
		   for(int b: a) {
			   System.out.print(b+" ");
		   }
		   System.out.println();
	   }
	   
   }

   	private static void createSol(){
   		int[][] sol=new int[size][size];
   		
   		int c=1;
   		for(int i =0;i<size;i++) {
   			for(int j =0;j<size;j++) {
   				sol[i][j]=c;
   				c++;
   			}
   		}
   		sol[size-1][size-1]=0;
   		
   		goal_state=sol;
   		sol=null;
   	}
}

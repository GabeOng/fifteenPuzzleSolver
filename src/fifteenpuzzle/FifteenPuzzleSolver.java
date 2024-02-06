package fifteenpuzzle;

import java.util.*;

public class FifteenPuzzleSolver {
    
    public static void main(String[] args) {
        int[][] board = {{1, 2, 3, 4, 5},
                         {6, 7, 8, 9, 10},
                         {11, 12, 13, 14, 15},
                         {16, 17, 18, 19, 20},
                         {21, 22, 23, 24, 0}};
        int[] goalState = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 0};
        State initialState = new State(board, 0, 0, null);
        solve(initialState, goalState);
    }
    
    public static void solve(State initialState, int[] goalState) {
        PriorityQueue<State> openSet = new PriorityQueue<>();
        Set<State> closedSet = new HashSet<>();
        openSet.add(initialState);
        
        while (!openSet.isEmpty()) {
            State current = openSet.poll();
            if (Arrays.equals(current.flatten(), goalState)) {
                System.out.println("Solution found!");
                printSolution(current);
                return;
            }
            closedSet.add(current);
            for (State neighbor : current.getNeighbors()) {
                if (!closedSet.contains(neighbor)) {
                    int tentativeCost = current.getCost() + 1;
                    if (!openSet.contains(neighbor) || tentativeCost < neighbor.getCost()) {
                        neighbor.setCost(tentativeCost);
                        neighbor.setHeuristic(neighbor.manhattanDistance());
                        neighbor.setTotalCost(neighbor.getCost() + neighbor.getHeuristic());
                        neighbor.setPrev(current);
                        if (!openSet.contains(neighbor)) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }
        System.out.println("No solution found.");
    }
    
    public static void printSolution(State state) {
        List<State> path = new ArrayList<>();
        while (state != null) {
            path.add(state);
            state = state.getPrev();
        }
        Collections.reverse(path);
        for (State s : path) {
            System.out.println("Move #" + s.getMoves());
            System.out.println(s);
        }
    }
}

class State implements Comparable<State> {
    private int[][] board;
    private int cost;
    private int moves;
    private int heuristic;
    private int totalCost;
    private State prev;
    
    public State(int[][] board, int cost, int moves, State prev) {
        this.board = board;
        this.cost = cost;
        this.moves = moves;
        this.prev = prev;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public int getCost() {
        return cost;
    }
    
    public int getMoves() {
        return moves;
    }
    
    public int getHeuristic() {
        return heuristic;
    }
    
    public int getTotalCost() {
        return totalCost;
    }
    
    public State getPrev() {
        return prev;
    }
    
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public void setMoves(int moves) {
        this.moves = moves;
    }
    
    public void setHeuristic(int heuristic) {
    	this.heuristic = heuristic;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setPrev(State prev) {
        this.prev = prev;
    }

    public List<State> getNeighbors() {
        List<State> neighbors = new ArrayList<>();
        int[] zeroPos = findZero();
        int zeroRow = zeroPos[0];
        int zeroCol = zeroPos[1];
        
        if (zeroRow > 0) { // move up
            int[][] newBoard = copyBoard(board);
            int temp = newBoard[zeroRow][zeroCol];
            newBoard[zeroRow][zeroCol] = newBoard[zeroRow - 1][zeroCol];
            newBoard[zeroRow - 1][zeroCol] = temp;
            State neighbor = new State(newBoard, cost, moves + 1, this);
            neighbors.add(neighbor);
        }
        if (zeroRow < 4) { // move down
            int[][] newBoard = copyBoard(board);
            int temp = newBoard[zeroRow][zeroCol];
            newBoard[zeroRow][zeroCol] = newBoard[zeroRow + 1][zeroCol];
            newBoard[zeroRow + 1][zeroCol] = temp;
            State neighbor = new State(newBoard, cost, moves + 1, this);
            neighbors.add(neighbor);
        }
        if (zeroCol > 0) { // move left
            int[][] newBoard = copyBoard(board);
            int temp = newBoard[zeroRow][zeroCol];
            newBoard[zeroRow][zeroCol] = newBoard[zeroRow][zeroCol - 1];
            newBoard[zeroRow][zeroCol - 1] = temp;
            State neighbor = new State(newBoard, cost, moves + 1, this);
            neighbors.add(neighbor);
        }
        if (zeroCol < 4) { // move right
            int[][] newBoard = copyBoard(board);
            int temp = newBoard[zeroRow][zeroCol];
            newBoard[zeroRow][zeroCol] = newBoard[zeroRow][zeroCol + 1];
            newBoard[zeroRow][zeroCol + 1] = temp;
            State neighbor = new State(newBoard, cost, moves + 1, this);
            neighbors.add(neighbor);
        }
        
        return neighbors;
    }

    public int[] findZero() {
        int[] pos = new int[2];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    pos[0] = i;
                    pos[1] = j;
                    return pos;
                }
            }
        }
        return pos;
    }

    public int manhattanDistance() {
        int distance = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int value = board[i][j];
                if (value != 0) {
                    int row = (value - 1) / 5;
                    int col = (value - 1) % 5;
                    distance += Math.abs(row - i) + Math.abs(col - j);
                }
            }
        }
        return distance;
    }

    public int[] flatten() {
        int[] flatBoard = new int[25];
        int k = 0;
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                flatBoard[k++] = board[i][j];
            }
        }
        return flatBoard;
    }

    public int gaschnigHeuristic() {
        int[] flatBoard = flatten();
        int h = 0;
        
        // check rows
        for (int i = 0; i < 25; i += 5) {
            for (int j = i; j < i + 4; j++) {
                if (flatBoard[j] > flatBoard[j + 1]) {
                    h += 2;
                }
            }
        }
        
        // check columns
        for (int i = 0; i < 5; i++) {
            for (int j = i; j < 20; j += 5) {
                if (flatBoard[j] > flatBoard[j + 5]) {
                    h += 2;
                }
            }
        }
        
        return manhattanDistance() + h;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

	@Override
	public int compareTo(State other) {
	    return (this.moves + this.manhattanDistance()) - (other.moves + other.manhattanDistance());
	}
}

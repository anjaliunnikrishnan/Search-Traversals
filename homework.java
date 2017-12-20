import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static java.lang.Math.exp;
import static java.lang.Math.log;

public class homework {

    static int frontier[][];
    private static int lizard_size;
    private static int i_pointer = 0;
    static int true_lizard_size;
    private static int tree_size;
    static int grid_size;
    static PrintWriter writer;
    static long start, end, end2;
    static int TREE = 2;
    static int FREE_POSITION = 0;
    static int OCCUPIED = 1;
    static long FINAL_TIME = 27000;
    //static boolean flag = false;

    public static void main(String[] args) throws Exception {
        Bfs bfs = new Bfs();
        Frontier f = new Frontier();
        writer = new PrintWriter("output.txt");
        FileReader fr = new FileReader("Bfs/DFS15.txt");
        BufferedReader br = new BufferedReader(fr);
        String algorithm = br.readLine();
        grid_size = Integer.parseInt(br.readLine());
        lizard_size = Integer.parseInt(br.readLine());
        frontier = new int[grid_size][grid_size];
        int row = 0, col;
        String line = "";
        while ((line = br.readLine()) != null) {
            char[] chars = line.toCharArray();
            for (col = 0; col < chars.length; col++) {
                frontier[row][col] = Integer.parseInt(String.valueOf(chars[col]));
                if (frontier[row][col] == TREE) {
                    tree_size += 1;
                }
            }
            row++;
        }
        br.close();
        true_lizard_size = lizard_size;
        start = System.currentTimeMillis();
        if ((tree_size == 0 && true_lizard_size > frontier[0].length) || (true_lizard_size > (grid_size * grid_size)) || (grid_size == 0)) {
            writer.print("FAIL");
        } else if (true_lizard_size == 0) {
            writer.println("OK");
            print_grid(frontier);
        } else if (tree_size == (grid_size * grid_size)) {
            writer.print("FAIL");
        } else {
            Simulated_annealing sm = new Simulated_annealing();
            switch (algorithm) {
                case "DFS":
                    if (!start_the_game(frontier)) {
                        writer.print("FAIL");
                    }
                    break;
                case "BFS":
                    f.create_frontier(frontier);
                    if (!(bfs.do_bfs(f))) {
                        writer.print("FAIL");
                    }
                    break;
                case "SA":
                    if (!sm.start_simulated_annealing(frontier)) {
                        writer.print("FAIL");
                    }
                    break;
            }
        }
        end2 = System.currentTimeMillis();
        System.out.println("Time Taken:" + (end2 - start));
        writer.flush();
        writer.close();
    }

    private static boolean dfs(int[][] frontier, int row, int col) throws IOException {
        end = System.currentTimeMillis();
        if ((end - start) > FINAL_TIME) {
            //flag = true;
            return false;
        }
        if (lizard_size == 0) {
            writer.print("OK\n");
            print_grid(frontier);
            return true;
        } else {
            for (int i = row; i < grid_size; i++) {
                for (int j = 0; j < grid_size; j++) {
                    if (frontier[i][j] == FREE_POSITION) {
                        frontier[i][j] = OCCUPIED;
                        lizard_size -= 1;
                        mark_unsafe_nodes(frontier, i, j, ++i_pointer);
                        if (!dfs(frontier, i, j)) {
                            frontier[i][j] = FREE_POSITION;
                            if (lizard_size + 1 <= true_lizard_size)
                                lizard_size += 1;
                            mark_safe_nodes(frontier, i, j, i_pointer);
                            if (i_pointer - 1 > -1)
                                i_pointer -= 1;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void mark_safe_nodes(int[][] frontier, int row, int col, int i_pointer) {
        //mark all columns in the same row safe --> Right
        for (int i = col + 1; i < grid_size; i++) {
            if (frontier[row][i] == TREE)
                break;
            else {
                if (frontier[row][i] == -i_pointer)
                    frontier[row][i] = FREE_POSITION;
            }
        }

        //mark all columns in the same row safe <-- Left
        for (int i = col - 1; i >= 0; i--) {
            if (frontier[row][i] == TREE)
                break;
            else {
                if (frontier[row][i] == -i_pointer)
                    frontier[row][i] = FREE_POSITION;
            }
        }

        //mark all rows int the same column safe --> Down
        for (int i = row + 1; i < grid_size; i++) {
            if (frontier[i][col] == TREE)
                break;
            else {
                if (frontier[i][col] == -i_pointer)
                    frontier[i][col] = FREE_POSITION;
            }
        }

        //mark all rows int the same column safe --> Up
        for (int i = row - 1; i >= 0; i--) {
            if (frontier[i][col] == TREE)
                break;
            else {
                if (frontier[i][col] == -i_pointer)
                    frontier[i][col] = FREE_POSITION;
            }
        }

        //mark left lower diagonal safe
        for (int i = row + 1, j = col - 1; i < grid_size && j >= 0; i++, j--) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == -i_pointer)
                frontier[i][j] = FREE_POSITION;
        }

        //mark right lower diagonal safe
        for (int i = row + 1, j = col + 1; i < grid_size && j < grid_size; i++, j++) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == -i_pointer)
                frontier[i][j] = FREE_POSITION;
        }

        //mark left upper diagonal safe
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == -i_pointer)
                frontier[i][j] = FREE_POSITION;
        }

        //mark right upper diagonal safe
        for (int i = row - 1, j = col + 1; i >= 0 && j < grid_size; i--, j++) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == -i_pointer)
                frontier[i][j] = FREE_POSITION;
        }
    }

    private static void mark_unsafe_nodes(int[][] frontier, int row, int col, int in) {
        //mark all columns in the same row unsafe --> right
        for (int i = col + 1; i < grid_size; i++) {
            if (frontier[row][i] == TREE)
                break;
            else {
                if (frontier[row][i] == FREE_POSITION)
                    frontier[row][i] = -in;
            }
        }
        //mark all columns in the same row unsafe <-- left
        for (int i = col - 1; i >= 0; i--) {
            if (frontier[row][i] == TREE)
                break;
            else {
                if (frontier[row][i] == FREE_POSITION)
                    frontier[row][i] = -in;
            }
        }
        //mark all rows int the same column unsafe --> Down
        for (int i = row + 1; i < grid_size; i++) {
            if (frontier[i][col] == TREE)
                break;
            else if (frontier[i][col] == FREE_POSITION)
                frontier[i][col] = -in;
        }

        //mark all rows int the same column unsafe --> Up
        for (int i = row - 1; i >= 0; i--) {
            if (frontier[i][col] == TREE)
                break;
            else if (frontier[i][col] == FREE_POSITION)
                frontier[i][col] = -in;
        }

        //mark left lower diagonal unsafe
        for (int i = row + 1, j = col - 1; i < grid_size && j >= 0; i++, j--) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == FREE_POSITION)
                frontier[i][j] = -in;
        }

        //mark right lower diagonal unsafe
        for (int i = row + 1, j = col + 1; i < grid_size && j < grid_size; i++, j++) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == FREE_POSITION)
                frontier[i][j] = -in;
        }

        //mark left upper diagonal safe
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == FREE_POSITION)
                frontier[i][j] = -in;
        }

        //mark right upper diagonal safe
        for (int i = row - 1, j = col + 1; i >= 0 && j < grid_size; i--, j++) {
            if (frontier[i][j] == TREE)
                break;
            else if (frontier[i][j] == FREE_POSITION)
                frontier[i][j] = -in;
        }
    }

    static void print_grid(int[][] frontier) throws IOException {
        for (int row = 0; row < grid_size; row++) {
            for (int col = 0; col < grid_size; col++) {
                if (frontier[row][col] == OCCUPIED || frontier[row][col] == TREE) {
                    writer.print(frontier[row][col]);
                } else {
                    writer.print(FREE_POSITION);
                }
            }
            writer.println();
        }
    }

    private static boolean start_the_game(int[][] frontier) throws IOException {
        if (tree_size == 0)
            if (lizard_size > grid_size)
                return false;
        return dfs(frontier, 0, 0);
    }
}

class Bfs {
    private Queue<Frontier> q = new LinkedList<>();

    boolean do_bfs(Frontier f) throws IOException {
        try {
            q.add(f);
            while (!q.isEmpty()) {
                homework.end = System.currentTimeMillis();
                if ((homework.end - homework.start) > homework.FINAL_TIME) {
                    return false;
                }
                Frontier parent_frontier = q.element();
                Frontier sub_frontier = new Frontier();
                q.remove();
                sub_frontier.create_frontier(parent_frontier.frontier);
                sub_frontier.row = parent_frontier.row;
                sub_frontier.col = parent_frontier.col;
                sub_frontier.lizard_size = parent_frontier.lizard_size;
                int k = parent_frontier.lizard_size;
                while (sub_frontier.get_next_sub_frontier()) {
                    if (sub_frontier.lizard_size == homework.true_lizard_size) {
                        homework.writer.println("OK");
                        print_grid(sub_frontier.frontier);
                        return true;
                    }
                    mark_unsafe_nodes(sub_frontier.frontier, sub_frontier.row, sub_frontier.col, sub_frontier.lizard_size);
                    int i, j;
                    i = sub_frontier.row;
                    j = sub_frontier.col; //k = sub_frontier.lizard_size;
                    if (j == sub_frontier.frontier[0].length - 1)
                        i += 1;
                    q.add(sub_frontier);
                    //creating for next iteration
                    sub_frontier = new Frontier();
                    sub_frontier.create_frontier(parent_frontier.frontier);
                    sub_frontier.col = (j + 1) % sub_frontier.frontier[0].length;
                    sub_frontier.row = i;
                    sub_frontier.lizard_size = parent_frontier.lizard_size;
                }
            }
        } catch (OutOfMemoryError o) {
            return false;
        }
        return false;
    }

    private void print_grid(int[][] frontier) throws IOException {
        for (int row = 0; row < frontier[0].length; row++) {
            for (int col = 0; col < frontier[0].length; col++) {
                if (frontier[row][col] == homework.OCCUPIED || frontier[row][col] == homework.TREE) {
                    homework.writer.print(frontier[row][col]);
                } else {
                    homework.writer.print(homework.FREE_POSITION);
                }
            }
            homework.writer.println();
        }
    }

    private void mark_unsafe_nodes(int[][] frontier, int row, int col, int in) {
        //mark all columns in the same row unsafe --> right
        for (int i = col + 1; i < frontier[0].length; i++) {
            if (frontier[row][i] == homework.TREE)
                break;
            else {
                if (frontier[row][i] == homework.FREE_POSITION)
                    frontier[row][i] = -in;
            }
        }
        //mark all columns in the same row unsafe <-- left
        for (int i = col - 1; i >= 0; i--) {
            if (frontier[row][i] == homework.TREE)
                break;
            else {
                if (frontier[row][i] == homework.FREE_POSITION)
                    frontier[row][i] = -in;
            }
        }
        //mark all rows int the same column unsafe --> Down
        for (int i = row + 1; i < frontier[0].length; i++) {
            if (frontier[i][col] == homework.TREE)
                break;
            else if (frontier[i][col] == homework.FREE_POSITION)
                frontier[i][col] = -in;
        }

        //mark all rows int the same column unsafe --> Up
        for (int i = row - 1; i >= 0; i--) {
            if (frontier[i][col] == homework.TREE)
                break;
            else if (frontier[i][col] == homework.FREE_POSITION)
                frontier[i][col] = -in;
        }

        //mark left lower diagonal unsafe
        for (int i = row + 1, j = col - 1; i < frontier[0].length && j >= 0; i++, j--) {
            if (frontier[i][j] == 2)
                break;
            else if (frontier[i][j] == 0)
                frontier[i][j] = -in;
        }

        //mark right lower diagonal unsafe
        for (int i = row + 1, j = col + 1; i < frontier[0].length && j < frontier[0].length; i++, j++) {
            if (frontier[i][j] == 2)
                break;
            else if (frontier[i][j] == 0)
                frontier[i][j] = -in;
        }

        //mark left upper diagonal safe
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (frontier[i][j] == 2)
                break;
            else if (frontier[i][j] == 0)
                frontier[i][j] = -in;
        }

        //mark right upper diagonal safe
        for (int i = row - 1, j = col + 1; i >= 0 && j < frontier[0].length; i--, j++) {
            if (frontier[i][j] == 2)
                break;
            else if (frontier[i][j] == 0)
                frontier[i][j] = -in;
        }
    }
}

class Simulated_annealing {
    private Random rand = new Random();
    private int current_conflicts;
    private int grid_size = homework.grid_size;
    private ArrayList<Node> arrayList = new ArrayList<>();
    private Node node = new Node();

    private int get_random(int grid_size) {
        return rand.nextInt(grid_size);
    }

    private boolean get_probability(double p) {
        double r = Math.random() / (Math.random() + 1);
        return r < p;
    }

    private int number_of_conflicts(int[][] frontier, int row, int col) {
        int conflicts = 0;
        //mark all columns in the same row safe --> Right
        for (int i = col + 1; i < grid_size; i++) {
            if (homework.frontier[row][i] == 2)
                break;
            if (homework.frontier[row][i] == 1)
                conflicts += 1;
        }
        //mark all columns in the same row safe <-- Left
        for (int i = col - 1; i >= 0; i--) {
            if (homework.frontier[row][i] == 2)
                break;
            if (homework.frontier[row][i] == 1)
                conflicts += 1;
        }
        //mark all rows int the same column safe --> Down
        for (int i = row + 1; i < grid_size; i++) {
            if (homework.frontier[i][col] == 2)
                break;
            if (homework.frontier[i][col] == 1)
                conflicts += 1;
        }
        //mark all rows int the same column safe --> Up
        for (int i = row - 1; i >= 0; i--) {
            if (homework.frontier[i][col] == 2)
                break;
            if (homework.frontier[i][col] == 1)
                conflicts += 1;
        }
        //mark left lower diagonal safe
        for (int i = row + 1, j = col - 1; i < grid_size && j >= 0; i++, j--) {
            if (homework.frontier[i][j] == 2)
                break;
            if (homework.frontier[i][j] == 1)
                conflicts += 1;
        }
        //mark right lower diagonal safe
        for (int i = row + 1, j = col + 1; i < grid_size && j < grid_size; i++, j++) {
            if (homework.frontier[i][j] == 2)
                break;
            if (homework.frontier[i][j] == 1)
                conflicts += 1;
        }
        //mark left upper diagonal safe
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (homework.frontier[i][j] == 2)
                break;
            if (homework.frontier[i][j] == 1)
                conflicts += 1;
        }
        //mark right upper diagonal safe
        for (int i = row - 1, j = col + 1; i >= 0 && j < grid_size; i--, j++) {
            if (homework.frontier[i][j] == 2)
                break;
            if (homework.frontier[i][j] == 1)
                conflicts += 1;
        }
        return conflicts;
    }

    private boolean do_simulated_annealing(int[][] frontier) throws IOException {
        int next_conflicts = 0;
        Node node2;
        double time = 1;
        while (time > 0) {
            homework.end = System.currentTimeMillis();
            if ((homework.end - homework.start) > 270000) {
                return false;
            }
            double T = schedule(time);
            // System.out.println("T value: "+T);
            if (T == 0) return false;
            int next_row = get_random(grid_size);
            int next_col = get_random(grid_size);
            if (homework.frontier[next_row][next_col] == 2 || homework.frontier[next_row][next_col] == 1) {
                do {
                    next_row = get_random(grid_size);
                    next_col = get_random(grid_size);
                } while (homework.frontier[next_row][next_col] != 0);
            }
            homework.frontier[next_row][next_col] = 1;
            int n = get_random(arrayList.size());
            node2 = arrayList.get(n);
            int current_row = arrayList.get(n).row;
            int current_col = arrayList.get(n).col;
            homework.frontier[current_row][current_col] = 0;
            //print_grid();
            arrayList.remove(node2);
            node2 = new Node();
            node2.row = next_row;
            node2.col = next_col;
            arrayList.add(node2);
            for (Node anArrayList : arrayList) {
                node2 = anArrayList;
                next_conflicts += number_of_conflicts(frontier, node2.row, node2.col);
            }
            int diff = next_conflicts - current_conflicts;
            // System.out.println("Energy difference "+diff);
            if (diff <= 0) {
                current_conflicts = next_conflicts;
            } else {
                double exp = exp((current_conflicts - next_conflicts) / T);
                // System.out.println("Energy difference/T "+exp);
                if (get_probability(exp)) {
                    current_conflicts = next_conflicts;
                } else {
                    homework.frontier[next_row][next_col] = 0;
                    arrayList.remove(node2);
                    node2 = new Node();
                    node2.row = current_row;
                    node2.col = current_col;
                    arrayList.add(node2);
                    homework.frontier[current_row][current_col] = 1;
                }
            }
            next_conflicts = 0;
            if (current_conflicts == 0) {
                homework.writer.print("OK\n");
                homework.print_grid(frontier);
                return true;
            }
            time += 1;
        }
        return false;
    }

    boolean start_simulated_annealing(int[][] frontier) throws IOException {
        int lizard_size = 0;
        current_conflicts = 0;
        while (lizard_size != homework.true_lizard_size) {
            homework.end = System.currentTimeMillis();
            if ((homework.end - homework.start) > 260000) {
                return false;
            }
            int row = get_random(grid_size);
            int col = get_random(grid_size);
            if (homework.frontier[row][col] == 0) {
                homework.frontier[row][col] = 1;
                lizard_size += 1;
                node.row = row;
                node.col = col;
                arrayList.add(node);
                node = new Node();
            }
        }
        if (homework.true_lizard_size == 1) {
            homework.writer.print("OK\n");
            homework.print_grid(frontier);
            return true;
        }
        for (Node anArrayList : arrayList) {
            current_conflicts += number_of_conflicts(frontier, anArrayList.row, anArrayList.col);
        }
        if (current_conflicts == 0) {
            homework.writer.print("OK\n");
            homework.print_grid(frontier);
            return true;
        }
        return do_simulated_annealing(frontier);
    }

    private double schedule(double time) {
        return (1 / (log(time)));
    }
}

class Node {
    int row;
    int col;

    Node() {
        row = 0;
        col = 0;
    }
}

class Frontier {

    int frontier[][];
    int row;
    int col;
    int lizard_size;

    Frontier() {
        lizard_size = 0;
        col = 0;
        row = 0;
    }

    void create_frontier(int[][] x) {
        frontier = new int[x[0].length][x[0].length];
        for (int i = 0; i < x[0].length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                frontier[i][j] = x[i][j];
            }
        }
    }

    boolean get_next_sub_frontier() {
        int k = 0;
        if (col != 0) {
            k = (col) % frontier[0].length;
        }
        for (int i = row; i < frontier[0].length; i++) {
            for (int j = k; j < frontier[0].length; j++) {
                if (frontier[i][j] == 0) {
                    frontier[i][j] = 1;
                    lizard_size++;
                    row = i;
                    col = j;
                    return true;
                }
            }
            k = 0;
        }
        return false;
    }
}

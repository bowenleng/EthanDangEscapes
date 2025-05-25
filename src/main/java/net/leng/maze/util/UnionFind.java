package net.leng.maze.util;

public class UnionFind {
    // needed for Kruskal's algo
    private int[] parent;
    private int cnt;
    public UnionFind(int size) {
        parent = new int[size];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
        cnt = parent.length;
    }

    public int find(int i) {
        while (parent[i] != i) {
            i = parent[i];
        }
        return i;
    }

    public int size() {
        return parent.length;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public void union(int i, int j) {
        int ir = find(i);
        int jr = find(j);
        parent[ir] = jr;
        cnt--;
    }

    public int getCount() {
        return cnt;
    }

    public void reset() {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
        cnt = parent.length;
    }
}

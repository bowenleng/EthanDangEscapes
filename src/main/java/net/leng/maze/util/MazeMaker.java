package net.leng.maze.util;

import java.awt.*;
import java.util.*;

public class MazeMaker {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;

    private final int[][] matrix;
    // 8 bits, they determine what they are connected to:
    // the below is 0 indexing:
    // digit 0 is left, digit 1 is right, digit 2 is up, digit 3 is down
    // digit 4 indicates whether the solver has visited the cell.
    // digit 5 indicates the path of the solver
    // digits 6-7 indicate the item
    private final int size;
    private final int sizeSq;
    private boolean altered;
    private int width, height;
    private final Random random;
    private final Stack<CellNode> dfsStack;
    private final Queue<CellNode> primsQueue;
    private int binTreeCellId = 0;
    private UnionFind uf;
    private Color leftUp;
    private Color rightUp;
    private Color leftDown;
    private Color rightDown;
    private final Stack<Integer> path;

    public MazeMaker(int size) {
        this(size, null);
    }

    public MazeMaker(int size, MazeMaker prev) {
        this.matrix = new int[size][size];
        this.size = size;
        this.sizeSq = size * size;
        this.random = new Random();
        this.dfsStack = new Stack<>();
        this.primsQueue = new PriorityQueue<>((a, b) -> this.random.nextInt(-1, 2));
        this.leftUp = prev == null ? generateColor() : prev.leftUp;
        this.rightUp = prev == null ? generateColor() : prev.rightUp;
        this.leftDown = prev == null ? generateColor() : prev.leftDown;
        this.rightDown = prev == null ? generateColor() : prev.rightDown;
        this.path = new Stack<>();
    }

    public void regenerateColor() {
        leftUp = generateColor();
        rightUp = generateColor();
        leftDown = generateColor();
        rightDown = generateColor();
    }

    public void generateItems() {
        // index 4 is webtoon stories, index 5 is cheese
        // if player comes in contact with 4, they gain a point
        // if player comes in contact with 5, they lose a point
        // possible more efficient method... a number can technically get stored in the bitwise...
        // for two digits, these are the options
        // 00 for no items
        // 01 for webtoon
        // 10 for cheese
        // 11 for rare webtoon
        int itemsLeft = sizeSq / 5;
        while (itemsLeft > 0) {
            int x = random.nextInt(0, size);
            int y = random.nextInt(0, size);
            if ((x != 0 || y != 0) && (x != size-1 || y != size-1) && ((matrix[x][y] >> 4) & 11) == 0) {
                matrix[x][y] |= random.nextInt(0, 3) == 0 ? 0b0100_0000 : (random.nextInt(0, sizeSq / 5) == 0 ? 0b1100_0000 : 0b1000_0000);
                itemsLeft--;
            }
        }
    }

    public boolean hasGoodStory(int x, int y) {
        return ((matrix[x][y] >> 6) & 11) == 3;
    }
    public boolean hasStory(int x, int y) {
        return ((matrix[x][y] >> 6) & 11) == 2;
    }

    public boolean hasCheese(int x, int y) {
        return ((matrix[x][y] >> 6) & 11) == 1;
    }

    public void removeItem(int x, int y) {
        matrix[x][y] &= 0b0011_1111;
    }

    public boolean dfs() {
        if (altered) {
            if (dfsStack.isEmpty())
                return true;
            CellNode node = dfsStack.pop();
            int dir = oppDir(node.dir);
            int x = node.id % size;
            int y = node.id / size;
            while (!dfsStack.isEmpty() && !notConnected(x, y)) {
                node = dfsStack.pop();
                dir = oppDir(node.dir);
                x = node.id % size;
                y = node.id / size;
            }
            if (matrix[x][y] == 0) {
                generateAPath(x, y, dir);
                int start = random.nextInt(0, 4);
                for (int i = start; i < start + 4; i++) {
                    int val = i % 4;
                    if (canGeneratePath(x, y, val)) {
                        dfsStack.push(new CellNode(addedId(x, y, val), val));
                    }
                }
            }
        } else {
            dfsStack.push(new CellNode(getId(0, 0), random.nextBoolean() ? 0 : 2));
            altered = true;
        }
        return false;
    }

    public boolean kruskals() {
        if (altered) {
            if (uf == null || uf.getCount() <= 1 || binTreeCellId != 0) return true;
            int id = random.nextInt(0, sizeSq);
            int x = id % size;
            int y = id / size;
            int invX = invalidXDir(x);
            int invY = invalidYDir(y);
            int rand = random.nextInt(0, 4 - (invX == 4 ? 0 : 1) - (invY == 4 ? 0 : 1));
            if (rand >= invX) rand++;
            if (rand >= invY) rand++;
            rand %= 4;
            int nId = addedId(x, y, rand);
            while (uf.connected(id, nId)) {
                id = random.nextInt(0, sizeSq);
                x = id % size;
                y = id / size;
                invX = invalidXDir(x);
                invY = invalidYDir(y);
                rand = random.nextInt(0, 4 - (invX == 4 ? 0 : 1) - (invY == 4 ? 0 : 1));
                if (rand >= invX) rand++;
                if (rand >= invY) rand++;
                rand %= 4;
                nId = addedId(x, y, rand);
            }
            uf.union(id, nId);
            generateAPath(x, y, rand);
        } else {
            restartUf();
            altered = true;
        }

        return false;
    }

    public boolean prims() {
        if (altered) {
            if (primsQueue.isEmpty())
                return true;
            CellNode node = primsQueue.poll();
            int dir = oppDir(node.dir);
            int x = node.id % size;
            int y = node.id / size;
            int opId = addedId(x, y, dir);
            // the while loop makes the drawing animation faster
            // it ensures that the drawing will change.
            while (!canGeneratePath(opId % size, opId / size, node.dir)) {
                node = primsQueue.poll();
                if (node == null) return true;
                x = node.id % size;
                y = node.id / size;
                dir = oppDir(node.dir);
                opId = addedId(x, y, dir);
            }
            if (canGeneratePath(opId % size, opId / size, node.dir)) {
                generateAPath(x, y, dir);
                int start = random.nextInt(0, 4);
                for (int i = start; i < start + 4; i++) {
                    int val = i % 4;
                    if (val == dir) continue;
                    int nId = addedId(x, y, val);
                    if (nId >= 0 && nId < sizeSq) {
                        primsQueue.offer(new CellNode(nId, val));
                    }
                }
            }
        } else {
            boolean rightFirst = random.nextBoolean();
            primsQueue.offer(rightFirst ? new CellNode(getId(1, 0), 1) : new CellNode(getId(0, 1), 3));
            primsQueue.offer(rightFirst ? new CellNode(getId(0, 1), 3) : new CellNode(getId(1, 0), 1));
            altered = true;
        }
        return false;
    }

    public boolean binaryTree() {
        if (altered) {
            if (binTreeCellId == sizeSq) return true;
            int x = binTreeCellId % size;
            int y = binTreeCellId / size;
            int dir = random.nextBoolean() ? 1 : 3;
            int nId = addedId(x, y, dir);
            if (validLocation(nId) && !uf.connected(nId, binTreeCellId)) {
                generateAPath(x, y, dir);
            } else {
                int alt = dir == 1 ? 3 : 1;
                int altId = addedId(x, y, alt);
                if (validLocation(altId) && !uf.connected(altId, binTreeCellId)) {
                    generateAPath(x, y, alt);
                }
            }
            binTreeCellId++;
        } else {
            int val = random.nextBoolean() ? 1 : 3;
            if (canGeneratePath(0, 0, val)) {
                restartUf();
                int nId = addedId(0, 0, val);
                uf.union(nId, 0);
                generateAPath(0, 0, val);
                binTreeCellId++;
                altered = true;
            }
        }
        return false;
    }

    public int getSize() {
        return size;
    }

    public void copyColor(MazeMaker maker) {
        leftUp = maker.leftUp;
        leftDown = maker.leftDown;
        rightUp = maker.rightUp;
        rightDown = maker.rightDown;
    }

    private void restartUf() {
        if (uf != null) {
            if (uf.size() != sizeSq) {
                uf = new UnionFind(sizeSq);
            } else {
                uf.reset();
            }
        } else {
            uf = new UnionFind(sizeSq);
        }
    }

    private boolean validLocation(int id) {
        int x = id % size;
        int y = id / size;
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private int addedId(int x, int y, int ind) {
        int nx = x + switch (ind) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
        int ny = y + switch (ind) {
            case UP -> -1;
            case DOWN -> 1;
            default -> 0;
        };
        if (nx < 0 || nx >= size || ny < 0 || ny >= size) return -1;
        return getId(nx, ny);
    }

    private int invalidXDir(int x) {
        if (x-1 < 0) {
            return LEFT;
        } else if (x+1 >= size) {
            return RIGHT;
        }
        return 4;
    }

    private int invalidYDir(int y) {
        if (y-1 < 0) {
            return UP;
        } else if (y+1 >= size) {
            return DOWN;
        }
        return 4;
    }

    // 0 means left, 1 means right, 2 means up, 3 means down
    private boolean canGeneratePath(int x, int y, int ind) {
        boolean unconnected = notConnected(x, y);
        if (ind == UP && y-1 >= 0 && ((matrix[x][y] >> 2) & 1) == 0) {
            return unconnected || notConnected(x, y-1);
        }
        if (ind == DOWN && y+1 < size && ((matrix[x][y] >> 3) & 1) == 0) {
            return unconnected || notConnected(x, y+1);
        }
        if (ind == LEFT && x-1 >= 0 && (matrix[x][y] & 1) == 0) {
            return unconnected || notConnected(x-1, y);
        }
        if (ind == RIGHT && x+1 < size && ((matrix[x][y] >> 1) & 1) == 0) {
            return unconnected || notConnected(x+1, y);
        }
        return false;
    }

    private boolean notConnected(int x, int y) {
        return (matrix[x][y] & 0b1111) == 0;
    }

    private void generateAPath(int x, int y, int ind) {
        if (ind == UP) {
            matrix[x][y] |= 0b0100;
            matrix[x][y-1] |= 0b1000;
            return;
        }
        if (ind == DOWN) {
            matrix[x][y] |= 0b1000;
            matrix[x][y+1] |= 0b0100;
            return;
        }
        if (ind == LEFT) {
            matrix[x][y] |= 0b0001;
            matrix[x-1][y] |= 0b0010;
            return;
        }
        if (ind == RIGHT) {
            matrix[x][y] |= 0b0010;
            matrix[x+1][y] |= 0b0001;
        }
    }

    private int getId(int x, int y) {
        return y * size + x;
    }

    private int oppDir(int ind) {
        return switch (ind) {
            case 1 -> 0;
            case 2 -> 3;
            case 3 -> 2;
            default -> 1;
        };
    }

    public void draw(Graphics g) {
        int interval = Math.min(width, height) / size;
        boolean shortHeight = height < width;
        int trimmedSize = interval * size;
        int added = (Math.max(height, width) - trimmedSize) / 2;
        for (int i = 0; i < size; i++) {
            int x1 = interval * i + (shortHeight ? added : 0);
            int x2 = x1 + interval;
            for (int j = 0; j < size; j++) {
                int val = matrix[i][j];
                int y1 = interval * j + (shortHeight ? 0 : added);
                int y2 = y1 + interval;
                if (((val >> 5) & 1) == 1) {
                    g.setColor(new Color(20, 46, 20));
                    g.fillRect(x1, y1, interval, interval);
                }

                g.setColor(mergeWithDist(i, j));
                if ((val & 1) == 0) {
                    g.drawLine(x1, y1, x1, y2);
                }
                if (((val >> 1) & 1) == 0 && (i != size - 1 || j != size - 1)) {
                    g.drawLine(x2, y1, x2, y2);
                }
                if (((val >> 2) & 1) == 0) {
                    g.drawLine(x1, y1, x2, y1);
                }
                if (((val >> 3) & 1) == 0) {
                    g.drawLine(x1, y2, x2, y2);
                }

                // todo refine the mathematical coordinates
                if (((val >> 6) & 11) == 3) {
                    if (ResourceDirectory.GOOD_WEBTOON != null) {
                        g.drawImage(ResourceDirectory.GOOD_WEBTOON, x1 + 1, y1 + 1, interval - 2, interval - 2, null);
                    } else {
                        g.setColor(Color.PINK);
                        g.fillOval(x1 + 1, y1 + 1, interval - 2, interval - 2);
                    }
                } else if (((val >> 6) & 11) == 2) {
                    if (ResourceDirectory.WEBTOON != null) {
                        g.drawImage(ResourceDirectory.WEBTOON, x1 + 1, y1 + 1, interval - 2, interval - 2, null);
                    } else {
                        g.setColor(Color.GREEN);
                        g.fillOval(x1 + 1, y1 + 1, interval - 2, interval - 2);
                    }
                } else if (((val >> 6) & 11) == 1) {
                    if (ResourceDirectory.CHEESE != null) {
                        g.drawImage(ResourceDirectory.CHEESE, x1 + 1, y1 + 1, interval - 2, interval - 2, null);
                    } else {
                        g.setColor(Color.YELLOW);
                        g.fillOval(x1 + 1, y1 + 1, interval - 2, interval - 2);
                    }
                }
            }
        }
    }

    private Color mergeWithDist(int x, int y) {
        int x1 = size - x;
        int y1 = size - y;
        int r = x1 * leftUp.getRed() + x * rightUp.getRed() + y1 * leftDown.getRed() + y * rightDown.getRed();
        int g = x1 * leftUp.getGreen() + x * rightUp.getGreen() + y1 * leftDown.getGreen() + y * rightDown.getGreen();
        int b = x1 * leftUp.getBlue() + x * rightUp.getBlue() + y1 * leftDown.getBlue() + y * rightDown.getBlue();
        int max = Math.max(r, Math.max(g, b));
        double mult = 255.0 / max;
        return new Color((int)(r * mult), (int)(g * mult), (int)(b * mult));
    }

    private Color generateColor() {
        int r = random.nextInt(0, 256);
        int g = random.nextInt(0, 256);
        int b = random.nextInt(0, 256);
        // if the color is not bright enough, increase brightness.
        int max = Math.max(r, Math.max(g, b));
        double mult = 255.0 / max;
        return new Color((int)(r * mult), (int)(g * mult), (int)(b * mult));
    }

    public void reset() {
        if (altered) {
            dfsStack.clear();
            primsQueue.clear();
            path.clear();
            binTreeCellId = 0;
            if (uf != null) uf.reset();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = 0;
                }
            }
            altered = false;
        }
    }

    public boolean canMoveDirection(int x, int y, int dir) {
        return switch (dir) {
            case LEFT -> (matrix[x][y] & 1) == 1 && x > 0;
            case RIGHT -> ((matrix[x][y] >> 1) & 1) == 1 && x < size-1;
            case UP -> ((matrix[x][y] >> 2) & 1) == 1 && y > 0;
            case DOWN -> ((matrix[x][y] >> 3) & 1) == 1 && y < size-1;
            default -> false;
        };
    }

    public int getNextPathId() {
        if (path.isEmpty()) return -1;
        int val = path.pop();
        matrix[val % size][val / size] |= 0b0010_0000;
        return val;
    }

    public boolean pathMade() {
        return !path.isEmpty();
    }

    public void pathFinder(int x, int y) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(this::compareDist);
        HashMap<Integer, Integer> pathMap = new HashMap<>();
        int orgId = getId(x, y);
        pq.offer(orgId);
        while (!pq.isEmpty()) {
            int id = pq.poll();
            if (id == sizeSq - 1) break;
            int nx = id % size;
            int ny = id / size;
            int val = matrix[nx][ny];
            if (notVisited(nx, ny)) {
                matrix[nx][ny] |= 0b0001_0000; // marks the cell as being visited
                if (nx - 1 >= 0 && (val & 1) == 1 && notVisited(nx-1, ny)) {
                    int nId = id-1;
                    pathMap.put(nId, id);
                    pq.offer(nId);
                }
                if (nx + 1 < size && ((val >> 1) & 1) == 1 && notVisited(nx+1, ny)) {
                    int nId = id+1;
                    pathMap.put(nId, id);
                    pq.offer(nId);
                }
                if (ny - 1 >= 0 && ((val >> 2) & 1) == 1 && notVisited(nx, ny-1)) {
                    int nId = id - size;
                    pathMap.put(nId, id);
                    pq.offer(nId);
                }
                if (ny + 1 < size && ((val >> 3) & 1) == 1 && notVisited(nx, ny+1)) {
                    int nId = id + size;
                    pathMap.put(nId, id);
                    pq.offer(nId);
                }
            }
        }
        int keyVal = sizeSq - 1;
        while (pathMap.containsKey(keyVal)) {
            if (keyVal == orgId) break;
            path.push(keyVal);
            keyVal = pathMap.get(keyVal);
        }
    }

    private boolean notVisited(int x, int y) {
        return ((matrix[x][y] >> 4) & 1) == 0;
    }

    private int compareDist(int a, int b) {
        double aDist = calcDistToEndFromId(a);
        double bDist = calcDistToEndFromId(b);
        if (aDist == bDist) return 0;
        return aDist > bDist ? 1 : -1;
    }

    private double calcDistToEndFromId(int id) {
        int x = id % size;
        int y = id / size;
        return Math.sqrt(Math.pow(size - 1 - x, 2) + Math.pow(size - 1 - y, 2));
    }

    static class CellNode {
        int id;
        int dir;
        CellNode(int id, int dir) {
            this.id = id;
            this.dir = dir;
        }
    }
}
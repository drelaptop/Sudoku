package com.laptop.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author laputa 暂 不考虑性能问题及数独方阵出错的可能,考虑数独唯一解的情况
 */
public class Sudoku {
    
    private final String FILE_NAME = "H:\\Code\\spaceGit\\Study\\src\\com\\laptop\\sudoku\\sudoku.ini";
    
    private final int[] SUDOKU_ITEM = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
    
    // 数独方阵
    private int[][] matrix = null;
    
    // 方阵位置的可选集合
    private int[][][] mtxOptSet = null;
    
    public Sudoku() {
        super();
        this.matrix = new int[9][9];
        this.mtxOptSet = new int[9][9][10];// 第十个位置存放可选集合的大小
    }
    
    public int[][] getMatrix() {
        return matrix;
    }
    
    /**
     * 待完成
     * 
     * @return
     */
    public boolean tryComplete() {
        this.dealMtxOptSet();
        return false;
    }
    
    /**
     * @param line
     *            行号：0-8 -1代表不取行，只取对应列
     * @param column
     *            列号：0-8 -1代表不取列，只取对应行
     * @return
     */
    private int[] getOptSetItem(int line, int column) {
        int[] temp = new int[9];// 自动初始化为0!
        if (-1 == line) {
            for (int i = 0; i < 9; i++) {
                temp[i] = this.matrix[i][column];
            }
        } else if (-1 == column) {
            for (int i = 0; i < 9; i++) {
                temp[i] = this.matrix[line][i];
            }
        } else {
            int count = 0;
            for (int i = line; i < line + 3; i++) {
                for (int j = column; j < column + 3; j++) {
                    temp[count++] = this.matrix[i][j];
                }
            }
        }
        return this.decArrSet(this.SUDOKU_ITEM, temp);
    }
    
    /**
     * 对每一个方阵中为空的值计算可选集合
     * 
     * @return 一个可选集合的二维数组
     */
    private void dealMtxOptSet() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (0 == this.matrix[i][j]) {
                    int[] lineSet = this.getOptSetItem(i, -1);
                    int[] columnSet = this.getOptSetItem(-1, j);
                    int[] squareSet = this.getOptSetItem(i - i % 3, j - j % 3);
                    this.mtxOptSet[i][j] = this.addArrSet(this.addArrSet(lineSet, columnSet), squareSet);
                    for (int p = 0; p < 9; p++) {
                        if (0 != this.mtxOptSet[i][j][p]) {
                            this.mtxOptSet[i][j][9]++;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 从文件读入方阵,存于成员变量:文件为9*9方阵,只出现字符[0-9],[0]代表方阵中对应位置为空
     */
    public boolean setMatrix() {
        int i = 0;
        int j = 0;
        int total = 0;
        int item = -1;
        boolean isSuccess = true;
        InputStreamReader reader = null;
        try {
            InputStream in = new FileInputStream(new File(FILE_NAME));
            reader = new InputStreamReader(in);
            while ((item = reader.read()) != -1) {
                if (this.isRightItem(item)) {
                    this.matrix[i][j] = item - '0';
                    i = ++total / 9;
                    j = ++j % 9;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        return isSuccess;
    }
    
    /**
     * @param ch
     *            读取的字符
     * @return 判断一个字符是否符合数独中字符的要求
     */
    private boolean isRightItem(int ch) {
        boolean isSuccess = false;
        for (int i = 0; i < 10; i++) {
            if (ch == SUDOKU_ITEM[i] + '0') {
                isSuccess = true;
            } else {
                isSuccess = false || isSuccess;
            }
        }
        return isSuccess;
    }
    
    /**
     * 控制台展示
     */
    public void showMatrix() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (0 == matrix[i][j]) {
                    System.out.printf("%-6s", "[" + this.mtxOptSet[i][j][9] + "]");
                } else {
                    System.out.printf("%-6s", matrix[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
    
    /**
     * [0-9]集合的补，0为无效字符
     * 
     * @param arrTotal
     *            [0-9] 10个
     * @param arrItem
     *            [0-9] 至多9个
     * @return
     */
    private int[] decArrSet(int[] arrTotal, int[] arrItem) {
        int[] arrResult = new int[9];
        System.arraycopy(arrTotal, 0, arrResult, 0, 9);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (arrResult[j] == arrItem[i]) {
                    arrResult[j] = 0;
                }
            }
        }
        return arrResult;
    }
    
    /**
     * [0-9]集合的交，0为无效字符
     * 
     * @param arrTotal
     * @param arrItem
     * @return
     */
    private int[] addArrSet(int[] arrItem1, int[] arrItem2) {
        int[] arrResult = new int[10];
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (arrItem1[i] == arrItem2[j] && arrItem1[i] != 0) {
                    arrResult[count++] = arrItem1[i];
                }
            }
        }
        return arrResult;
    }
}

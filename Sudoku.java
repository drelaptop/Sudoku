package com.laptop.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author laputa 前提：数独具有唯一解
 */
public class Sudoku {
    
    /**
     * 数独可能出现数值的集合，'0'代表空
     */
    private final int[] SUDOKU_ITEM = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
    
    /**
     * 计算数独花费的次数
     */
    private int dealTimes = 0;
    
    /**
     * 数独矩阵
     */
    private int[][] matrix = null;
    
    /**
     * 数独矩阵每一位置可选值得集合
     */
    private int[][][] mtxOptSet = null;
    
    /**
     * 通过一个文件初始化数组矩阵
     * 
     * @param filePath
     */
    public Sudoku(String filePath) {
        super();
        this.matrix = new int[9][9];
        this.mtxOptSet = new int[9][9][10];// 第十个位置存放可选集合的大小
        this.setMatrix(filePath);
    }
    
    /**
     * 解数独,统计求解过程中循环处理的次数
     * 
     * @return 正常处理完成/由于循环次数太多被迫中止
     */
    public boolean deal() {
        boolean isSuccess = true;
        while (this.dealMtxOptSet() != 0) {
            this.dealTimes++;
            // 处理次数大于81,说明数独无解,不再处理
            if (this.dealTimes > 81) {
                isSuccess = false;
                break;
            }
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (1 == this.mtxOptSet[i][j][9]) {
                        this.matrix[i][j] = this.arrSum(this.mtxOptSet[i][j]);
                        this.mtxOptSet[i][j] = new int[10];
                    }
                }
            }
        }
        return isSuccess;
    }
    
    /**
     * 验证解是否符合数独的规则
     * 
     * @return 符合/不符合
     */
    public boolean judge() {
        boolean dealSuccess = true;
        outer: for (int i = -1; i < 9; i++) {
            for (int j = -1; j < 9; j++) {
                if (-1 == i && -1 == j) {
                    break;
                }
                int[] arrTest = this.arrSort(this.mtx2Arr(i, j));
                for (int k = 0; k < 9; k++) {
                    if (k + 1 != arrTest[k]) {
                        dealSuccess = false;
                        break outer;
                    }
                }
            }
        }
        return dealSuccess;
    }
    
    /**
     * @param line
     *            行号：0-8 -1代表不取行，只取对应列
     * @param column
     *            列号：0-8 -1代表不取列，只取对应行
     * @return 可选值的集合,'0'代表空
     */
    private int[] getOptSetItem(int line, int column) {
        int[] temp = mtx2Arr(line, column);
        return this.decArrSet(this.SUDOKU_ITEM, temp);
    }
    
    /**
     * @param line
     *            行号：0-8 -1代表不取行，只取对应列
     * @param column
     *            列号：0-8 -1代表不取列，只取对应行
     * @return 数独中需要满足为[1-9]的区域值的集合
     */
    private int[] mtx2Arr(int line, int column) {
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
            int blockI = line - line % 3;
            int blockJ = column - column % 3;
            // 元素所在的3*3矩阵转Array
            for (int i = blockI; i < blockI + 3; i++) {
                for (int j = blockJ; j < blockJ + 3; j++) {
                    temp[count++] = this.matrix[i][j];
                }
            }
        }
        return temp;
    }
    
    /**
     * 对每一个矩阵中为空的值计算可选集合
     * 
     * @return 一个可选集合的二维数组
     */
    private int dealMtxOptSet() {
        int maxOptSet = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (0 == this.matrix[i][j]) {
                    int[] lineSet = this.getOptSetItem(i, -1);
                    int[] columnSet = this.getOptSetItem(-1, j);
                    int[] squareSet = this.getOptSetItem(i, j);
                    this.mtxOptSet[i][j] = this.addArrSet(this.addArrSet(lineSet, columnSet), squareSet);
                    for (int p = 0; p < 9; p++) {
                        if (0 != this.mtxOptSet[i][j][p]) {
                            this.mtxOptSet[i][j][9]++;
                        }
                        if (mtxOptSet[i][j][9] > maxOptSet) {
                            maxOptSet = mtxOptSet[i][j][9];
                        }
                    }
                }
            }
        }
        return maxOptSet;
    }
    
    /**
     * 从文件读入矩阵,存于成员变量:文件为9*9矩阵,只出现字符[0-9],[0]代表矩阵中对应位置为空
     */
    private boolean setMatrix(String fileName) {
        int i = 0;
        int j = 0;
        int total = 0;
        int item = -1;
        boolean isSuccess = true;
        InputStreamReader reader = null;
        try {
            InputStream in = new FileInputStream(new File(fileName));
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
    public void show() {
        System.out.println("deal times:" + this.dealTimes);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (0 == matrix[i][j]) {
                    int optNum = this.mtxOptSet[i][j][9];
                    if (0 != optNum) {
                        System.out.printf("%-4s", "[" + this.mtxOptSet[i][j][9] + "]");
                    } else {
                        System.out.printf("%-4s", " * ");
                    }
                } else {
                    System.out.printf("%-4s", " " + matrix[i][j] + " ");
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
    
    /**
     * 数组含9个int值
     * 
     * @param arr
     *            数组
     * @return 数组的和
     */
    private int arrSum(int[] arr) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += arr[i];
        }
        return sum;
    }
    
    /**
     * 数组含9个int值
     * 
     * @param arr
     *            初始数组
     * @return 从小到大排序好的数组
     */
    private int[] arrSort(int[] arr) {
        for (int i = 0; i < 9; i++) {
            for (int j = i; j < 9; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[j];
                    arr[j] = arr[i];
                    arr[i] = temp;
                }
            }
        }
        return arr;
    }
}

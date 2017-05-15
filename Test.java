package com.laptop.sudoku;

public class Test {
    
    public static void main(String[] args) throws Exception {
        Sudoku sudoKu = new Sudoku();
        sudoKu.setMatrix();
        sudoKu.tryComplete();
        sudoKu.showMatrix();
    }
    
}

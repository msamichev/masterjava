package ru.javaops.masterjava.matrix;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws Exception {
        final CompletionService<Pair<Integer, int[][]>> completionService = new ExecutorCompletionService<>(executor);

        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        List<Future<Pair<Integer, int[][]>>> futures = new ArrayList<>();
        //for (int i = 0, r = matrixSize; i < matrixSize; i += MainMatrix.THREAD_NUMBER, r -= MainMatrix.THREAD_NUMBER) {

        int count = matrixSize/MainMatrix.THREAD_NUMBER;

        for (int i = 0; i < MainMatrix.THREAD_NUMBER; i++) {
            final int idx = i * count;
            final int[][] subMatrixB = matrixB;
            final int[][] subMatrixA = new int[count][matrixSize];

            for (int j = 0; j < subMatrixA.length; j++) {
                subMatrixA[j] = matrixA[idx + j];
            }
            futures.add(completionService.submit(() -> new Pair<>(idx, singleThreadMultiply(subMatrixA, subMatrixB))));
        }

        return ((Callable<int[][]>) () -> {

            while (!futures.isEmpty()) {
                Future<Pair<Integer, int[][]>> future = completionService.poll(10, TimeUnit.SECONDS);
                if (future != null) {
                    futures.remove(future);
                    Pair<Integer, int[][]> pair = future.get();
                    int idx = pair.getKey();
                    int[][] subMatrix = pair.getValue();
                    for (int i = 0; i < subMatrix.length; i++) {
                        matrixC[idx + i] = subMatrix[i];
                    }
                }
            }
            return matrixC;
        }).call();

    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixARows = matrixA.length;
        final int matrixAColumns = matrixA[0].length;
        final int matrixBRows = matrixB.length;
        final int matrixBColumns = matrixB[0].length;
        final int[][] matrixC = new int[matrixARows][matrixBColumns];

        int[] thatColumn = new int[matrixBRows];

        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < matrixAColumns; k++) {
                    thatColumn[k] = matrixB[k][j];
                }

                for (int i = 0; i < matrixARows; i++) {
                    int[] thisRow = matrixA[i];
                    int sum = 0;
                    for (int k = 0; k < matrixAColumns; k++) {
                        sum += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = sum;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}

package com.example.tictactoe.tictactoe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WinCheckService {

    public boolean checkWinner(String[] board, int boardSize, int lastMoveRow, int lastMoveCol, String player) {

        int count = 0;

        //region END SOMEONE WIN

        // region HORIZONTAL
        for (int horizontal = 0; horizontal < boardSize; horizontal++) {
            if (board[lastMoveRow * boardSize + horizontal].equals(player)) count++;
        }
        if (count == boardSize) return true;
        // endregion [-]

        //region VERTICAL [|]
        count = 0;
        for (int vertical = 0; vertical < boardSize; vertical++) {
            if (board[vertical * boardSize + lastMoveCol].equals(player)) count++;
        }
        if (count == boardSize) return true;
        //endregion

        //region DIAGONAL [/]
        count = 0;
        for (int diag = 0; diag < boardSize; diag++) {
            if (board[diag * boardSize + diag].equals(player)) count++;
        }
        if (count == boardSize) return true;
        //endregion

        //region ANTI-DIAGONAL [\]
        count = 0;
        for (int antiDiag = 0; antiDiag < boardSize; antiDiag++) {
            if (board[antiDiag * boardSize + (boardSize - 1 - antiDiag)].equals(player)) count++;
        }
        if (count == boardSize) return true;
        //endregion

        //endregion

        //region NOT END
        return false;
        //endregion
    }

    public boolean isWinningStillPossible(String[] board, int boardSize) {
        // CHECK ROW
        for (int r = 0; r < boardSize; r++) {
            if (isLineOpen(board, r, 0, 0, 1, boardSize)) return true; // (r, c, dRow, dCol)
        }

        // CHECK COL
        for (int c = 0; c < boardSize; c++) {
            if (isLineOpen(board, 0, c, 1, 0, boardSize)) return true;
        }

        // CHECK DIAG
        if (isLineOpen(board, 0, 0, 1, 1, boardSize)) return true;

        // CHECK ANTI-DIAG
        if (isLineOpen(board, 0, boardSize - 1, 1, -1, boardSize)) return true;

        // ALL BLOCKED
        return false;
    }

    private boolean isLineOpen(String[] board, int r, int c, int dr, int dc, int boardSize) {
        boolean hasX = false;
        boolean hasO = false;

        for (int i = 0; i < boardSize; i++) {
            int currentRow = r + i * dr;
            int currentCol = c + i * dc;
            String cell = board[currentRow * boardSize + currentCol];

            if (cell.equals("X")) {
                hasX = true;
            } else if (cell.equals("O")) {
                hasO = true;
            }
            if (hasX && hasO) {
                return false;
            }
        }
        return true;
    }
}

package org.rmc.buscaminas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    private Spinner spinnerRows;
    private Spinner spinnerCols;

    private TableRow.LayoutParams params;

    private TableLayout tableLayout;
    private List<TableRow> tableRows;

    private int[][] board;

    private boolean running;

    private int cellsToWin;
    private int numberOfMines;

    private TextView minesText;
    private TextView resultMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerRows = findViewById(R.id.spinnerRows);
        spinnerCols = findViewById(R.id.spinnerCols);
        fillSpinners();

        linearLayout = findViewById(R.id.verticalLayout);

        tableLayout = new TableLayout(this);
        linearLayout.addView(tableLayout);

        minesText = new TextView(this);
        minesText.setTextSize(20);
        minesText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        resultMsg = new TextView(this);
        resultMsg.setGravity(Gravity.CENTER);
        resultMsg.setTextSize(30);
        resultMsg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 2));

        LinearLayout infoBar = new LinearLayout(this);
        infoBar.addView(minesText);
        infoBar.addView(resultMsg);

        linearLayout.addView(infoBar);

        tableRows = new ArrayList<>();
        params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this::onClickCreateButton);
    }

    public void onClickCreateButton(View v) {
        clearBoard();

        int rows = Integer.parseInt(spinnerRows.getSelectedItem().toString());
        int cols = Integer.parseInt(spinnerCols.getSelectedItem().toString());

        fillBoard(rows, cols);
        drawBoard();
        minesText.setText(String.format(getResources().getString(R.string.mines_msg), numberOfMines));
        resultMsg.setText("");

        running = true;
    }

    public void onClickCellButton(View v) {
        ButtonCell properties = (ButtonCell) v.getTag();
        if (running && !properties.isInAlert()) {
            if (properties.isMine()) {
                running = false;
                showAllMines();
                resultMsg.setTextColor(Color.RED);
                resultMsg.setText(getResources().getString(R.string.lose_msg));
            } else {
                if (properties.getNumber() == 0) {
                    showNearbyCells(properties.getX(), properties.getY());
                } else if (properties.isHidden()) {
                    findViewById(v.getId()).setBackground(ContextCompat.getDrawable(this,
                            R.drawable.cell_background_show));
                    ((Button) findViewById(v.getId())).setText(String.valueOf(properties.getNumber()));
                    properties.setHidden(false);
                    cellsToWin--;
                }
            }
            if (cellsToWin == 0) {
                resultMsg.setTextColor(Color.GREEN);
                resultMsg.setText(getResources().getString(R.string.win_msg));
                running = false;
            }
        }
    }

    public boolean onLongClickPutAlert(View v) {
        if (running) {
            ButtonCell properties = (ButtonCell) v.getTag();
            if (properties.isHidden()) {
                Button cell = findViewById(v.getId());
                if (properties.isInAlert()) {
                    cell.setBackground(ContextCompat.getDrawable(this,
                            R.drawable.cell_background_hide));
                    cell.setText("");
                    properties.setInAlert(false);
                    numberOfMines++;
                } else {
                    cell.setBackground(ContextCompat.getDrawable(this,
                            R.drawable.cell_background_alert));
                    cell.setText("?");
                    properties.setInAlert(true);
                    numberOfMines--;
                }
                minesText.setText(String.format(getResources().getString(R.string.mines_msg),
                        numberOfMines));
            }
        }
        return true;
    }

    private void fillSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.nums, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRows.setAdapter(adapter);
        spinnerCols.setAdapter(adapter);
    }

    private void fillBoard(int rows, int cols) {
        board = new int[rows][cols];
        numberOfMines = rows * cols / 4;
        cellsToWin = rows * cols - numberOfMines;

        putMines(numberOfMines);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                increaseNearbyCell(i, j);
            }
        }
    }

    private void increaseNearbyCell(int x, int y) {
        if (board[x][y] != -1) {
            if ((x - 1) >= 0) {
                if ((y - 1) >= 0 && board[x-1][y-1] == -1)
                    board[x][y]++;
                if (board[x-1][y] == -1)
                    board[x][y]++;
                if ((y + 1) < board[x].length && board[x-1][y+1] == -1)
                    board[x][y]++;
            }
            if ((y - 1) >= 0 && board[x][y-1] == -1)
                board[x][y]++;
            if ((y + 1) < board[x].length && board[x][y+1] == -1)
                board[x][y]++;
            if ((x + 1) < board.length) {
                if ((y - 1) >= 0 && board[x+1][y-1] == -1)
                    board[x][y]++;
                if (board[x+1][y] == -1)
                    board[x][y]++;
                if ((y + 1) < board[x].length && board[x+1][y+1] == -1)
                    board[x][y]++;
            }
        }
    }

    private void putMines(int numberOfMines) {
        Random random = new Random();
        while (numberOfMines > 0) {
            int x = random.nextInt(board.length);
            int y = random.nextInt(board[0].length);
            if (board[x][y] == 0) {
                board[x][y] = -1;
                numberOfMines--;
            }
        }
    }

    private void drawBoard() {
        int id = 1;
        for (int i = 0; i < board.length; i++) {
            tableRows.add(new TableRow(this));
            for (int j = 0; j < board[i].length; j++) {
                Button btn = new Button(this);
                btn.setId(id++);
                btn.setLayoutParams(params);
                btn.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.cell_background_hide));
                btn.setTag(new ButtonCell(i, j, board[i][j], board[i][j] == -1));
                btn.setText("");
                btn.setOnClickListener(this::onClickCellButton);
                btn.setOnLongClickListener(this::onLongClickPutAlert);
                tableRows.get(i).addView(btn);
            }
            tableLayout.addView(tableRows.get(i));
        }

    }

    private void clearBoard() {
        board = null;
        for (TableRow row : tableRows)
            row.removeAllViews();
        tableRows.clear();
        tableLayout.removeAllViews();
    }

    private void showAllMines() {
        for (TableRow row : tableRows) {
            for (int i = 0; i < row.getChildCount(); i++) {
                Button cell = (Button) row.getChildAt(i);
                ButtonCell properties = (ButtonCell) cell.getTag();
                if (properties.isMine()) {
                    cell.setBackground(ContextCompat.getDrawable(this,
                            R.drawable.cell_background_mine));
                }
            }
        }
    }

    private void showNearbyCells(int x, int y) {
        Button cell = (Button) tableRows.get(x).getChildAt(y);
        cell.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background_show));
        ButtonCell properties = (ButtonCell) cell.getTag();
        properties.setHidden(false);
        cellsToWin--;
        if (properties.getNumber() != 0) {
            cell.setText(String.valueOf(properties.getNumber()));
        } else {
            ButtonCell p;
            // --------- TOP ------------
            if ((x - 1) >= 0) {
                // Top - Left
                if ((y - 1) >= 0) {
                    p = (ButtonCell) tableRows.get(x - 1).getChildAt(y - 1).getTag();
                    if (p.isHidden())
                        showNearbyCells(x - 1, y - 1);
                }
                // Top
                p = (ButtonCell) tableRows.get(x - 1).getChildAt(y).getTag();
                if (p.isHidden())
                    showNearbyCells(x - 1, y);
                // Top - Right
                if ((y + 1) < tableRows.get(x - 1).getChildCount()) {
                    p = (ButtonCell) tableRows.get(x - 1).getChildAt(y + 1).getTag();
                    if (p.isHidden())
                        showNearbyCells(x - 1, y + 1);
                }
            }
            // --------- CENTER ------------
            // Left
            if ((y - 1) >= 0) {
                p = (ButtonCell) tableRows.get(x).getChildAt(y - 1).getTag();
                if (p.isHidden())
                    showNearbyCells(x, y - 1);
            }
            // Right
            if ((y + 1) < tableRows.get(x).getChildCount()) {
                p = (ButtonCell) tableRows.get(x).getChildAt(y + 1).getTag();
                if (p.isHidden())
                    showNearbyCells(x, y + 1);
            }
            // --------- BOTTOM ------------
            if ((x + 1) < tableRows.size()) {
                // Top - Left
                if ((y - 1) >= 0) {
                    p = (ButtonCell) tableRows.get(x + 1).getChildAt(y - 1).getTag();
                    if (p.isHidden())
                        showNearbyCells(x + 1, y - 1);
                }
                // Top
                p = (ButtonCell) tableRows.get(x + 1).getChildAt(y).getTag();
                if (p.isHidden())
                    showNearbyCells(x + 1, y);
                // Top - Right
                if ((y + 1) < tableRows.get(x + 1).getChildCount()) {
                    p = (ButtonCell) tableRows.get(x + 1).getChildAt(y + 1).getTag();
                    if (p.isHidden())
                        showNearbyCells(x + 1, y + 1);
                }
            }
        }
    }
}
package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import dao.BaseDAO;
import model.ReservationVO;
import util.Session;

@SuppressWarnings("serial")
public class MyReservationPanel extends JPanel {

    private final BaseDAO dao = new BaseDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private final JButton btnRefresh = new JButton("새로고침");
    private final JButton btnCancel = new JButton("예매취소(선택)");

    public MyReservationPanel() {
        setLayout(new BorderLayout(8, 8));

        String[] cols = {"예매번호", "영화", "날짜", "시간", "상영관", "좌석", "가격", "예매시간"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnRefresh);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshAsync());
        btnCancel.addActionListener(e -> cancelSelected());

        refreshAsync();
    }

    public void refresh() {
        refreshAsync();
    }

    private void refreshAsync() {
        if (!Session.isLoggedIn()) return;

        setBusy(true);
        new SwingWorker<List<ReservationVO>, Void>() {
            @Override protected List<ReservationVO> doInBackground() {
                return dao.myReservations(Session.getUser().getUserId());
            }

            @Override protected void done() {
                try {
                    List<ReservationVO> list = get();
                    model.setRowCount(0);
                    for (ReservationVO vo : list) {
                        model.addRow(new Object[]{
                                vo.getResId(),
                                vo.getMovieTitle(),
                                vo.getShowDate(),
                                vo.getShowTime(),
                                vo.getTheater(),
                                vo.getSeatNo(),
                                vo.getPrice(),
                                vo.getReservedAt()
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MyReservationPanel.this, "예매내역 로딩 실패");
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "취소할 예매를 선택하세요.");
            return;
        }

        int resId = (int) model.getValueAt(row, 0);

        int ok = JOptionPane.showConfirmDialog(
                this,
                "예매번호 " + resId + " 를 취소할까요?",
                "확인",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        setBusy(true);
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return dao.cancel(Session.getUser().getUserId(), resId);
            }

            @Override protected void done() {
                try {
                    boolean done = get();
                    if (!done) JOptionPane.showMessageDialog(MyReservationPanel.this, "취소 실패");
                    else JOptionPane.showMessageDialog(MyReservationPanel.this, "취소 완료");
                    refreshAsync();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    
                    Throwable cause = ex.getCause();
                    String msg = (cause != null) ? cause.getMessage() : ex.getMessage();

                    
                    JOptionPane.showMessageDialog(MyReservationPanel.this, "예매내역 로딩 실패\n원인 : " + msg);
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        btnRefresh.setEnabled(!busy);
        btnCancel.setEnabled(!busy);
    }
}

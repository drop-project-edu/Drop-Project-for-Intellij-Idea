import com.intellij.icons.AllIcons;
import com.intellij.ui.components.labels.BoldLabel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.dropProject.dropProjectPlugin.assignmentComponents.AssignmentTableLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AssignmentTableModel extends ListTableModel<AssignmentTableLine> {
    public AssignmentTableModel(ColumnInfo[] columnNames, @NotNull List<AssignmentTableLine> tableLines) {
        super(columnNames, tableLines);
    }

    static final String[] COLUMNS = {"", "Name", "Language", "Due Date", "Details"};

    public static ColumnInfo<AssignmentTableLine, String>[] generateColumnInfo() {
        ColumnInfo<AssignmentTableLine, String>[] columnInfos = new ColumnInfo[COLUMNS.length];
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(COLUMNS).forEach(eachColumn -> {
                    columnInfos[i.get()] = new ColumnInfo<>(eachColumn) {
                        @Nullable
                        @Override
                        public String valueOf(AssignmentTableLine o) {
                            switch (eachColumn) {
                                case "Name":
                                    return o.name;
                                case "Language":
                                    return o.language;
                                case "Due Date":
                                    return o.dueDate;
                                case "Details":
                                case "":
                                    return null;
                                default:
                                    return "Not Available";
                            }
                        }

                        @Override
                        public TableCellRenderer getCustomizedRenderer(AssignmentTableLine o, TableCellRenderer renderer) {
                            switch (eachColumn) {
                                case "Details":
                                    return (table, value, isSelected, hasFocus, row, column) -> new JButton("Instructions", AllIcons.Actions.Find);
                                case "":
                                    return (table, value, isSelected, hasFocus, row, column) -> o.radioButton;
                                default:
                                    return (table, value, isSelected, hasFocus, row, column) -> new BoldLabel(value.toString());
                            }
                        }

                    };
                    i.getAndIncrement();
                }
        );
        return columnInfos;
    }
}

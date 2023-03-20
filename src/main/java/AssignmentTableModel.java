import com.intellij.icons.AllIcons;
import com.intellij.ui.components.labels.BoldLabel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.TableLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AssignmentTableModel extends ListTableModel<TableLine> {
    public AssignmentTableModel(ColumnInfo @NotNull [] columnNames, @NotNull List<TableLine> tableLines) {
        super(columnNames, tableLines);
    }

    static final String[] COLUMNS = {"", "Name", "Language", "Due Date", "Details"};

    public static ColumnInfo<TableLine, String>[] generateColumnInfo() {
        ColumnInfo<TableLine, String>[] columnInfos = new ColumnInfo[COLUMNS.length];
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(COLUMNS).forEach(eachColumn -> {
                    columnInfos[i.get()] = new ColumnInfo<>(eachColumn) {
                        @Nullable
                        @Override
                        public String valueOf(TableLine o) {
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
                        public TableCellRenderer getCustomizedRenderer(TableLine o, TableCellRenderer renderer) {
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

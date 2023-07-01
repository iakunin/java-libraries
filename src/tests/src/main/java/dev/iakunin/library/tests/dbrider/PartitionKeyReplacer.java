package dev.iakunin.library.tests.dbrider;

import com.github.database.rider.core.replacers.Replacer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.dbunit.dataset.ReplacementDataSet;

public class PartitionKeyReplacer implements Replacer {

    @Override
    public void addReplacements(ReplacementDataSet dataSet) {
        dataSet.addReplacementSubstring(
            "@partition_key",
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"))
        );
    }
}

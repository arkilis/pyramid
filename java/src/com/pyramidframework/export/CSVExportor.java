package com.pyramidframework.export;

import java.nio.charset.Charset;

import com.csvreader.CsvWriter;

public class CSVExportor extends AbstractDBExportor {
	CsvWriter csvWriter = null;

	protected void beforeWriteData() {
		super.beforeWriteData();

		csvWriter = new CsvWriter(stream, ',', Charset.defaultCharset());

	}

	protected void writeCell(int row, int column, String data) throws Exception {
		if (column == 0 && row > 0) {
			csvWriter.endRecord();
		}
		csvWriter.write(data);
	}
	
	protected void afterWriteData() {
		super.afterWriteData();
		csvWriter.flush();
	}

}

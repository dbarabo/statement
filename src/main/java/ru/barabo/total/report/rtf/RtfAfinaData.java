package ru.barabo.total.report.rtf;

public interface RtfAfinaData {

	String procedureName();

	String procedureCallSql();

	Object[] paramCall();

	Long bbrId();
}

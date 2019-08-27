package ru.barabo.total.resources.owner;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "${cfgtotal}/msg.properties" })
public interface Msg extends Config {

	@DefaultValue("ошибка подключения к базе данных")
	String errorNotAccessDB();

	@DefaultValue("Запись не находится в состоянии правки/вставки")
	String errorNotFoundSave();

	@DefaultValue("При попытки удаления записи не был найден id")
	String errorDelIdNotFound();

	@DefaultValue("Невозможно изменить данные у поля %s")
	String errorNotUpdateField(String className);

	@DefaultValue("Невозможно перенести файл %s")
	String errorNotRemove(String fileName);

	@DefaultValue("Файл не найден %s")
	String fileNotFound(String fileName);
}

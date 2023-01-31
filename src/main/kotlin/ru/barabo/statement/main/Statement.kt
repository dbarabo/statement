package ru.barabo.statement.main

import ru.barabo.db.SessionSetting
import ru.barabo.statement.afina.AfinaQuery
import ru.barabo.statement.main.gui.Start
import ru.barabo.xls.*
import java.awt.Container
import javax.swing.JComboBox

object Statement {

    private val sessionSetting: SessionSetting = AfinaQuery.uniqueRollBackOnlySession()

    lateinit var clientVar: Var
    private set

    fun runApplication() {
       Start()
    }

    fun addSearchClients(container: Container, yPos: Int): JComboBox<ComboArray> {

        val querySelect = "{ ? = call od.xls_report_all.getClientJuricCredit }"

        val params = ArrayList<ReturnResult>()

        val cursor = CursorData(QuerySession(AfinaQuery, sessionSetting), querySelect, params)

        cursor.reopen(0)

        clientVar = Var("Клиент_юр_лицо", cursor.emptyRecord(0) )

        return container.comboSearch(clientVar, cursor, yPos, emptyList())
    }
}

fun main(args:Array<String>) {

    Statement.runApplication()
}
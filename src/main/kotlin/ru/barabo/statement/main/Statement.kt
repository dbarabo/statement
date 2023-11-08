package ru.barabo.statement.main

import ru.barabo.db.SessionSetting
import ru.barabo.statement.afina.AfinaQuery
import ru.barabo.statement.main.gui.Start
import ru.barabo.xls.*
import java.awt.Container
import javax.swing.JComboBox
import javax.swing.JTextField

object Statement {

    private val sessionSetting: SessionSetting = AfinaQuery.uniqueRollBackOnlySession()

    lateinit var clientVar: Var
    private set

    lateinit var sign1FioVar: Var
    private set

    lateinit var sign1PositionVar: Var
    private set

    lateinit var sign2FioVar: Var
    private set

    lateinit var sign2PositionVar: Var
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

    fun addSignFio1(container: Container, yPos: Int): JTextField {
        val (sign, field) = addText(container, yPos, "1_я_подпись_фио")

        sign1FioVar = sign

        return field
    }

    fun addSignPosition1(container: Container, yPos: Int): JTextField {
        val (sign, field) = addText(container, yPos, "1_я_подпись_должность")

        sign1PositionVar = sign

        return field
    }

    fun addSignFio2(container: Container, yPos: Int): JTextField {
        val (sign, field) = addText(container, yPos, "2_я_подпись_фио")

        sign2FioVar = sign

        return field
    }

    fun addSignPosition2(container: Container, yPos: Int): JTextField {
        val (sign, field) = addText(container, yPos, "2_я_подпись_должность")

        sign2PositionVar = sign

        return field
    }

    private fun addText(container: Container, yPos: Int, label: String): Pair<Var, JTextField> {

        val varText = Var(label, VarResult(VarType.VARCHAR, "") )

        return Pair(varText, container.textField(varText, yPos, ::varResultTextFieldListener ))
    }
}

fun main(args:Array<String>) {

    Statement.runApplication()
}
package com.example.feature_medicine.data

import android.annotation.SuppressLint
import android.util.Log
import com.example.feature_medicine.R
import com.example.global_data.data.db.Event
import com.example.global_data.data.Medicine
import com.example.global_data.data.db.MedicineDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MedicineRepository(
        private val medicineDatabase: MedicineDatabase
) {

    companion object {
        private const val TAG = "MedicineRepository"

        private const val NO_WARNING: Int = 0
        private const val FEW_MEDICINE_LEFT: Int = 1
        private const val MEDICINE_EXPIRING: Int = 2
        private const val NO_MEDICINE_LEFT: Int = 3
        private const val MEDICINE_EXPIRED: Int = 4
    }

    private var medicines = mutableListOf<Medicine>()
    private var events = mutableListOf<Event>()
    private val emptyElement = MedicineWarningElement(R.drawable.ic_example_capsules_warning, "", "")

    //default stuff
    val medicineCategoriesList = arrayListOf("Аллергия", "Covid-19", "Витамины и БАД", "Гастрит", "Диабет", "Дыхательная система", "Зрение", "Избыточный вес", "Изжога", "Кожные заболевания", "Личная гигиена", "Насморк", "Отит", "Обезболивающее", "Проблемы с пищеварением и кишечником", "Простуда и грипп", "Сердечно-сосудистые", "Другое")
    val medicineTypeList = arrayListOf("Ампулы", "Граммы", "Дозы спрея", "Ингаляции", "Инъекции", "Использования", "Капли", "Капсулы", "Милиграммы", "Миллилитры", "Пакетики", "Пластыри", "Свечи", "Таблетки", "Штуки")
    val oftennessList = arrayListOf("Ежедневно", "Еженедельно", "По необходимости")

    private fun getStringFromItemType(medicineType: String): String {
        when (medicineType) {
            "Ампулы" -> return "ампул"
            "Инъекции" -> return "инъекций"
            "Использования" -> return "использований"
            "Свечи" -> return "свеч"
            "Пакетик" -> return "пакетиков"
            "Пластырь" -> return "пластырей"
            "Таблетки" -> return "таблеток"
            "Штуки" -> return "штук"
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMedicineInfoElementsList(): ArrayList<MedicineInfoItem> {
        val medicineInfoItemsList: ArrayList<MedicineInfoItem> = arrayListOf()
        for (item in medicines) {
            val message = isMedicineItemHasWarning(item)

            val amountNumber: Int = if (item.isAmountCountable) {
                item.medicineCurrentAmount
            } else {
                Integer.MAX_VALUE
            }

            val dateFormat = SimpleDateFormat("dd/MM/yy")
            val expirationDate = dateFormat.parse(item.expirationDate)
            val expirationTime: Long = (expirationDate.time / 1000)

            val amountString: String = if (item.isAmountCountable) {
                "Ост. ${item.medicineCurrentAmount}/${item.medicineMaxAmount} ${getStringFromItemType(item.medicineType)}"
            } else {
                item.medicineMaxAmount
            }

            medicineInfoItemsList.add(MedicineInfoItem(item.medicineName,
                    amountString,
                    item.expirationDate,
                    amountNumber,
                    expirationTime,
                    message))
        }
        return medicineInfoItemsList
    }

    @JvmName("getMedicineWarningElementsList1")
    @SuppressLint("SimpleDateFormat")
    fun getMedicineWarningElementsList(list: List<Medicine>): ArrayList<MedicineWarningElement> {
        val medicineWarningElementsList: ArrayList<MedicineWarningElement> = arrayListOf(emptyElement)
        val secondsInAMonth: Long = 30 * 24 * 60 * 60
        val dateFormat = SimpleDateFormat("dd/MM/yy")

        for (item in list) {
            //checking for date expiration
            val currentDate = Date()
            val expirationDate = dateFormat.parse(item.expirationDate)

            //checking if there are drugs left
            if (item.isAmountCountable) {
                if (item.medicineCurrentAmount == 0) {
                    medicineWarningElementsList.add(MedicineWarningElement(R.drawable.ic_example_capsules_warning,
                            "Лекарство закончилось",
                            item.medicineName))
                    continue
                } else if (item.medicineCurrentAmount <= 3) {
                    medicineWarningElementsList.add(MedicineWarningElement(R.drawable.ic_example_capsules_warning,
                            "Лекарство заканчивается",
                            item.medicineName))
                    continue
                }
            }

            if (expirationDate.time - currentDate.time <= 0) {
                medicineWarningElementsList.add(MedicineWarningElement(R.drawable.ic_example_capsules_warning,
                        "Срок годности истёк",
                        item.medicineName))
                continue
            } else if ((expirationDate.time - currentDate.time) / 1000 <= secondsInAMonth) {
                medicineWarningElementsList.add(MedicineWarningElement(R.drawable.ic_example_capsules_warning,
                        "Срок годности истекает",
                        item.medicineName))
                continue
            }
        }
        return medicineWarningElementsList
    }

    @SuppressLint("SimpleDateFormat")
    private fun isMedicineItemHasWarning(item: Medicine): Int {
        val secondsInAMonth: Long = 30 * 24 * 60 * 60
        val dateFormat = SimpleDateFormat("dd/MM/yy")
        val currentDate = Date()
        val expirationDate = dateFormat.parse(item.expirationDate)
        if (expirationDate.time - currentDate.time <= 0) {
            return MEDICINE_EXPIRED
        }
        if ((expirationDate.time - currentDate.time) / 1000 <= secondsInAMonth) {
            return MEDICINE_EXPIRING
        }

        if (item.isAmountCountable) {
            if (item.medicineCurrentAmount <= 0) {
                return NO_MEDICINE_LEFT
            }
            if (item.medicineCurrentAmount <= 3) {
                return FEW_MEDICINE_LEFT
            }
        }
        return NO_WARNING
    }

    suspend fun getMedicinesList(): List<Medicine> {
        val medicines = medicineDatabase.medicinesDao().fetchAll()
        this.medicines.clear()
        this.medicines.addAll(medicines)
        Log.d(TAG, "Size of medicines ${medicines.size}")
        return medicines
    }

    suspend fun getEventsList(): List<Event> {
        val events = medicineDatabase.eventsDao().fetchAll()
        this.events.clear()
        this.events.addAll(events)
        Log.d(TAG, "Size of events ${events.size}")
        return events
    }

    suspend fun insertMedicine(medicine: Medicine) {
        medicineDatabase.medicinesDao().insert(medicine)
        Log.d(TAG, "inserted medicine $medicine")
    }

    suspend fun updateMedicine(medicine: Medicine) {
        medicineDatabase.medicinesDao().update(medicine)
        Log.d(TAG, "update medicine $medicine")
    }

    suspend fun insertEvent(event: Event) {
        medicineDatabase.eventsDao().insert(event)
        Log.d(TAG, "inserted event $event")
    }

    suspend fun deleteMedicine(medicine: Medicine){
        medicineDatabase.medicinesDao().delete(medicine)
        Log.d(TAG, "deleted medicine $medicine")
    }
}
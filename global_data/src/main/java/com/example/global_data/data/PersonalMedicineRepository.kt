package com.example.global_data.data

class PersonalMedicineRepository {
    var medicineWholeData: ArrayList<Medicine> = arrayListOf()

    init {
        medicineWholeData.add(Medicine(0, "Гексоген", "Пшик", "", "", "11/08/2021", "9/08/2021", "",
        "150мл.", 20, false, "", "", 0))
    }
}
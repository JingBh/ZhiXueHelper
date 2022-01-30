package top.jingbh.zhixuehelper.data.exam

enum class AssignedScore {
    A1, A2, A3, A4, A5,
    B1, B2, B3, B4, B5,
    C1, C2, C3, C4, C5,
    D1, D2, D3, D4, D5,
    E;

    fun getScore(): Double {
        return when (this) {
            A1 -> 100.0
            A2 -> 97.0
            A3 -> 94.0
            A4 -> 91.0
            A5 -> 88.0
            B1 -> 85.0
            B2 -> 82.0
            B3 -> 79.0
            B4 -> 76.0
            B5 -> 73.0
            C1 -> 70.0
            C2 -> 67.0
            C3 -> 64.0
            C4 -> 61.0
            C5 -> 58.0
            D1 -> 55.0
            D2 -> 52.0
            D3 -> 49.0
            D4 -> 46.0
            D5 -> 43.0
            E -> 40.0
        }
    }
}

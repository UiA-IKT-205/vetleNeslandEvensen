package no.uia.ikt205.piano.data

data class Note(val value:String, val start:Long, val end:Long){

    override fun toString(): String {
        return "$value,$start,$end"
    }
}
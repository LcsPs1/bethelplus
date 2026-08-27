package com.example.data.model

enum class SoundscapeType(val id: String, val displayName: String, val description: String) {
    HARP("HARPE", "Harpe Céleste", "Notes harmoniques douces et apaisantes"),
    CHIME("CLOCHE", "Cloche du Sanctuaire", "Carillon doux marquant les temps sacrés"),
    RAIN("PLUIE", "Pluie Apaisante", "Ruissellement calme d'une averse bienfaisante"),
    BREEZE("BRISE", "Brise d'Éden", "Souffle léger et murmurant"),
    SILENCE("SILENCE", "Silence Sacré", "Méditation en silence pur avec vibration douce")
}

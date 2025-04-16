package com.pandemiagame.org.data.remote.models

import com.pandemiagame.org.R

enum class Card(val displayName: String, val drawable: Int) {
    // Virus (IDs 41-45)
    VIRUS_HEART("Virus Heart", R.drawable.virus_heart),
    VIRUS_INTESTINE("Virus Intestine", R.drawable.virus_intestine),
    VIRUS_BRAIN("Virus Brain",  R.drawable.virus_brain),
    VIRUS_LUNGS("Virus Lungs",  R.drawable.virus_lungs),
    VIRUS_MAGIC("Virus Magic",  R.drawable.virus_magic),

    // Cura (IDs 46-50)
    CURE_HEART("Cure Heart",  R.drawable.cure_heart),
    CURE_INTESTINE("Cure Intestine",  R.drawable.cure_intestine),
    CURE_BRAIN("Cure Brain", R.drawable.cure_brain),
    CURE_LUNGS("Cure Lungs", R.drawable.cure_lungs),
    CURE_MAGIC("Cure Magic", R.drawable.cure_magic),

    // Órganos (IDs 51-55)
    ORGAN_HEART("Heart Organ", R.drawable.organ_heart),
    ORGAN_INTESTINE("Intestine Organ", R.drawable.organ_intestine),
    ORGAN_BRAIN("Brain Organ", R.drawable.organ_brain),
    ORGAN_LUNGS("Lungs Organ", R.drawable.organ_lungs),
    ORGAN_MAGIC("Magic Organ", R.drawable.magic),

    // Acciones (IDs 56-60)
    STEAL_ORGAN("Steal Organ", R.drawable.steal),
    INFECT_PLAYER("Infect Player", R.drawable.sneeze),
    EXCHANGE_CARD("Exchange Card", R.drawable.organ_change),
    CHANGE_BODY("Change Body", R.drawable.body_change),
    DISCARD_CARDS("Discard Cards", R.drawable.discard_cards);

    companion object {
        private val nameMap = Card.entries.associateBy { it.displayName }

        fun fromDisplayName(name: String): Card? {
            return nameMap[name]
        }
    }
}
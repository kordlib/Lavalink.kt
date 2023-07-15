@file:GenerateKordEnum(
    "Category",
    GenerateKordEnum.ValueType.STRING,
    "https://github.com/topi314/Sponsorblock-Plugin#segment-categories",
    entries = [
        GenerateKordEnum.Entry("Sponsor", stringValue = "sponsor"),
        GenerateKordEnum.Entry("Selfpromo", stringValue = "selfpromo"),
        GenerateKordEnum.Entry("Interaction", stringValue = "interaction"),
        GenerateKordEnum.Entry("Intro", stringValue = "intro"),
        GenerateKordEnum.Entry("Outro", stringValue = "outro"),
        GenerateKordEnum.Entry("Preview", stringValue = "preview"),
        GenerateKordEnum.Entry("MusicOfftopic", stringValue = "music_offtopic"),
        GenerateKordEnum.Entry("Filler", stringValue = "filler")
    ]
)

package dev.schlaubi.lavakord.plugins.sponsorblock.model

import dev.kord.ksp.GenerateKordEnum

@file:Generate(
    Generate.EntityType.STRING_KORD_ENUM,
    "Category",
    "https://github.com/topi314/Sponsorblock-Plugin#segment-categories",
    entries = [
        Generate.Entry("Sponsor", stringValue = "sponsor"),
        Generate.Entry("Selfpromo", stringValue = "selfpromo"),
        Generate.Entry("Interaction", stringValue = "interaction"),
        Generate.Entry("Intro", stringValue = "intro"),
        Generate.Entry("Outro", stringValue = "outro"),
        Generate.Entry("Preview", stringValue = "preview"),
        Generate.Entry("MusicOfftopic", stringValue = "music_offtopic"),
        Generate.Entry("Filler", stringValue = "filler")
    ]
)

package dev.schlaubi.lavakord.plugins.sponsorblock.model

import dev.kord.ksp.Generate

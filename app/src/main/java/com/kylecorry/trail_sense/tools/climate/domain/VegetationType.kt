package com.kylecorry.trail_sense.tools.climate.domain

import com.kylecorry.trail_sense.shared.data.Identifiable

enum class VegetationType(override val id: Long) : Identifiable {
    // https://codes.ecmwf.int/grib/param-db/29 and https://codes.ecmwf.int/grib/param-db/30
    CropsMixedFarming(1),
    Grass(2),
    EvergreenNeedleleafTrees(3),
    DeciduousNeedleleafTrees(4),
    DeciduousBroadleafTrees(5),
    EvergreenBroadleafTrees(6),
    TallGrass(7),
    Desert(8),
    Tundra(9),
    IrrigatedCrops(10),
    Semidesert(11),
    IceCapsAndGlaciers(12),
    BogsAndMarshes(13),
    InlandWater(14),
    Ocean(15),
    EvergreenShrubs(16),
    DeciduousShrubs(17),
    MixedForestWoodland(18),
    InterruptedForest(19),
    WaterAndLandMixtures(20);
}
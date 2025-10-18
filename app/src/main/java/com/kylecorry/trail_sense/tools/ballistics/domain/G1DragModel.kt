package com.kylecorry.trail_sense.tools.ballistics.domain

/*
* Calculated from https://www.jbmballistics.com/ballistics/downloads/text/g1.txt
* Using the V and T(V) column to determine change in velocity over time, downsampled for reduced size.
* Converted to m/s
 */
class G1DragModel(bc: Float = 1f) : TabulatedDragModel(bc) {
    override val dragTable: Map<Float, Float> = mapOf(
        1341.12f to 609.6f,
        1310.64f to 609.6f,
        1280.16f to 508.0f,
        1249.68f to 609.6f,
        1219.2f to 508.0f,
        1188.72f to 435.43f,
        1158.24f to 435.43f,
        1127.76f to 435.43f,
        1097.28f to 435.43f,
        1066.8f to 381.0f,
        1036.32f to 381.0f,
        1005.84f to 338.67f,
        975.36f to 338.67f,
        944.88f to 304.8f,
        914.4f to 304.8f,
        883.92f to 277.09f,
        853.44f to 277.09f,
        822.96f to 254.0f,
        792.48f to 234.46f,
        762.0f to 217.71f,
        731.52f to 217.71f,
        701.04f to 203.2f,
        670.56f to 190.5f,
        640.08f to 179.29f,
        609.6f to 152.4f,
        579.12f to 152.4f,
        548.64f to 138.55f,
        518.16f to 121.92f,
        487.68f to 108.86f,
        457.2f to 95.25f,
        426.72f to 82.38f,
        396.24f to 67.73f,
        365.76f to 52.55f,
        335.28f to 36.29f,
        304.8f to 21.93f,
        274.32f to 13.55f,
        243.84f to 9.18f,
        213.36f to 6.66f,
        182.88f to 4.7f,
        152.4f to 3.34f,
        121.92f to 2.23f,
        91.44f to 1.33f,
        60.96f to 0.63f,
        30.48f to 0.17f
    )
}
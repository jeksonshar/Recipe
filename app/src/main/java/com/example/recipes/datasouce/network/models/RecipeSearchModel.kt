package com.example.recipes.datasouce.network.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeSearchModel(
    @SerializedName("_links") val links: LinksModel? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("from") val from: Int? = null,
    @SerializedName("hits") val hits: List<HitModel>? = null,
    @SerializedName("to") val to: Int? = null
)

@Serializable
data class LinksModel(
    @SerializedName("next") val next: Next? = null
)

@Serializable
data class HitModel(
    @SerializedName("_links") val linksModel: LinksXModel? = null,
    @SerializedName("recipe") val recipeModel: RecipeModel? = null
)

@Serializable
data class Next(
    @SerializedName("href") val href: String? = null,
    @SerializedName("title") val title: String? = null
)

@Serializable
data class LinksXModel(
    @SerializedName("self") val self: Self? = null
)

@Serializable
data class RecipeModel(
    @SerializedName("calories") val calories: Double? = null,
    @SerializedName("cautions") val cautions: List<String>? = null,
    @SerializedName("cuisineType") val cuisineType: List<String>? = null,
    @SerializedName("dietLabels") val dietLabels: List<String>? = null,
//    @SerialName("digest") val digest: List<DigestEntity>? = null,
    @SerializedName("dishType") val dishType: List<String>? = null,
    @SerializedName("healthLabels") val healthLabels: List<String>? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("images") val images: ImagesModel? = null,
    @SerializedName("ingredientLines") val ingredientLines: List<String>? = null,
    @SerializedName("ingredients") val ingredients: List<IngredientModel>? = null,
    @SerializedName("label") val label: String? = null,
    @SerializedName("mealType") val mealType: List<String>? = null,
    @SerializedName("shareAs") val shareAs: String? = null,
    @SerializedName("source") val source: String? = null,
//    @SerialName("totalDaily") val totalDaily: TotalDailyEntity? = null,
//    @SerialName("totalNutrients") val totalNutrients: TotalNutrientsEntity? = null,
    @SerializedName("totalTime") val totalTime: Double? = null,
    @SerializedName("totalWeight") val totalWeight: Double? = null,
    @SerializedName("uri") val uri: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("yield") val yield: Double? = null
)

@Serializable
data class Self(
    @SerializedName("href") val href: String? = null,
    @SerializedName("title") val title: String? = null
)

//@Serializable
//data class DigestEntity(
//    @SerialName("daily") val daily: Double? = null,
//    @SerialName("hasRDI") val hasRDI: Boolean? = null,
//    @SerialName("label") val label: String? = null,
//    @SerialName("schemaOrgTag") val schemaOrgTag: String? = null,
//    @SerialName("sub") val sub: List<SubEntity>? = null,
//    @SerialName("tag") val tag: String? = null,
//    @SerialName("total") val total: Double? = null,
//    @SerialName("unit") val unit: String? = null
//)

@Serializable
data class ImagesModel(
    @SerializedName("LARGE") val large: LargeModel? = null,
    @SerializedName("REGULAR") val regular: RegularModel? = null,
    @SerializedName("SMALL") val small: SmallModel? = null,
    @SerializedName("THUMBNAIL") val thumbnail: ThumbnailModel? = null
)

@Serializable
data class IngredientModel(
    @SerializedName("food") val food: String? = null,
    @SerializedName("foodCategory") val foodCategory: String? = null,
    @SerializedName("foodId") val foodId: String? = null,
    /*@SerialName("image") */val image: String? = null,
    @SerializedName("measure") val measure: String? = null,
    @SerializedName("quantity") val quantity: Double? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("weight") val weight: Double? = null
)

//@Serializable
//data class TotalDailyEntity(
//    @SerialName("CA") val ca: Ca? = null,
//    @SerialName("CHOCDF") val chocdf: Chocdf? = null,
//    @SerialName("CHOLE") val chole: Chole? = null,
//    @SerialName("ENERC_KCAL") val enercKcal: Enerckcal? = null,
//    @SerialName("FASAT") val fasat: Fasat? = null,
//    @SerialName("FAT") val fat: Fat? = null,
//    @SerialName("FE") val fe: Fe? = null,
//    @SerialName("FIBTG") val fibtg: Fibtg? = null,
//    @SerialName("FOLDFE") val foldfe: Foldfe? = null,
//    @SerialName("K") val k: K? = null,
//    @SerialName("MG") val mg: MG? = null,
//    @SerialName("NA") val na: NA? = null,
//    @SerialName("NIA") val nia: Nia? = null,
//    @SerialName("P") val p: P? = null,
//    @SerialName("PROCNT") val procnt: Procnt? = null,
//    @SerialName("RIBF") val ribf: Ribf? = null,
//    @SerialName("THIA") val thia: Thia? = null,
//    @SerialName("TOCPHA") val tocpha: Tocpha? = null,
//    @SerialName("VITA_RAE") val vitaRae: Vitarae? = null,
//    @SerialName("VITB12") val vitb12: Vitb12? = null,
//    @SerialName("VITB6A") val vitb6a: Vitb6a? = null,
//    @SerialName("VITC") val vitc: VitC? = null,
//    @SerialName("VITD") val vitd: VitD? = null,
//    @SerialName("VITK1") val vitk1: Vitk1? = null,
//    @SerialName("ZN") val zn: ZN? = null
//)

//@Serializable
//data class TotalNutrientsEntity(
//    @SerialName("CA") val ca: CAX? = null,
//    @SerialName("CHOCDF") val chocdf: CHOCDFX? = null,
//    @SerialName("CHOLE") val chole: CHOLEX? = null,
//    @SerialName("ENERC_KCAL") val enercKcal: ENERCKCALX? = null,
//    @SerialName("FAMS") val fams: FAMS? = null,
//    @SerialName("FAPU") val fapu: FAPU? = null,
//    @SerialName("FASAT") val fasat: FASATX? = null,
//    @SerialName("FAT") val fat: FATX? = null,
//    @SerialName("FATRN") val fatrn: FATRN? = null,
//    @SerialName("FE") val fe: FEX? = null,
//    @SerialName("FIBTG") val fibtg: FIBTGX? = null,
//    @SerialName("FOLAC") val folac: FOLAC? = null,
//    @SerialName("FOLDFE") val foldfe: FOLDFEX? = null,
//    @SerialName("FOLFD") val folfd: FOLFD? = null,
//    @SerialName("K") val k: KX? = null,
//    @SerialName("MG") val mg: MGX? = null,
//    @SerialName("NA") val na: NAX? = null,
//    @SerialName("NIA") val nia: NIAX? = null,
//    @SerialName("P") val p: PX? = null,
//    @SerialName("PROCNT") val procnt: PROCNTX? = null,
//    @SerialName("RIBF") val ribf: RIBFX? = null,
//    @SerialName("SUGAR") val sugar: SUGAR? = null,
//    @SerialName("SUGAR.added") val sugarAdded: SUGARAdded? = null,
//    @SerialName("THIA") val thia: THIAX? = null,
//    @SerialName("TOCPHA") val tocpha: TOCPHAX? = null,
//    @SerialName("VITA_RAE") val vitaRae: VITARAEX? = null,
//    @SerialName("VITB12") val vitb12: VITB12X? = null,
//    @SerialName("VITB6A") val vitb6a: VITB6AX? = null,
//    @SerialName("VITC") val vitc: VITCX? = null,
//    @SerialName("VITD") val vitd: VITDX? = null,
//    @SerialName("VITK1") val vitk1: VITK1X? = null,
//    @SerialName("WATER") val water: WATER? = null,
//    @SerialName("ZN") val zn: ZNX? = null
//)

//@Serializable
//data class SubEntity(
//    @SerialName("daily") val daily: Double? = null,
//    @SerialName("hasRDI") val hasRDI: Boolean? = null,
//    @SerialName("label") val label: String? = null,
//    @SerialName("schemaOrgTag") val schemaOrgTag: String? = null,
//    @SerialName("tag") val tag: String? = null,
//    @SerialName("total") val total: Double? = null,
//    @SerialName("unit") val unit: String? = null
//)

@Serializable
data class LargeModel(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)
@Serializable
data class RegularModel(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)
@Serializable
data class SmallModel(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)
@Serializable
data class ThumbnailModel(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)

/*

@Serializable
data class Ca(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Chocdf(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Chole(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Enerckcal(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Fasat(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Fat(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Fe(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Fibtg(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Foldfe(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class K(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class MG(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class NA(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Nia(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class P(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Procnt(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Ribf(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Thia(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Tocpha(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Vitarae(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Vitb12(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Vitb6a(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VitC(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VitD(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class Vitk1(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class ZN(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class CAX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class CHOCDFX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class CHOLEX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class ENERCKCALX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FAMS(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FAPU(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FASATX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FATX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FATRN(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FEX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FIBTGX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FOLAC(
    val label: String,
    val quantity: Int,
    val unit: String
)
@Serializable
data class FOLDFEX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class FOLFD(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class KX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class MGX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class NAX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class NIAX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class PX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class PROCNTX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class RIBFX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class SUGAR(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class SUGARAdded(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class THIAX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class TOCPHAX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VITARAEX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VITB12X(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VITB6AX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VITCX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VITDX(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class VITK1X(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class WATER(
    val label: String,
    val quantity: Double,
    val unit: String
)
@Serializable
data class ZNX(
    val label: String,
    val quantity: Double,
    val unit: String
)*/
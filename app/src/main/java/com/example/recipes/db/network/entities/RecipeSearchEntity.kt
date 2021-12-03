package com.example.recipes.db.network.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeSearchEntity(
    @SerialName("_links") val links: LinksEntity? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("from") val from: Int? = null,
    @SerialName("hits") val hits: List<HitEntity>? = null,
    @SerialName("to") val to: Int? = null
)

@Serializable
data class LinksEntity(
    @SerialName("next") val next: Next? = null
)

@Serializable
data class HitEntity(
    @SerialName("_links") val linksEntity: LinksXEntity? = null,
    @SerialName("recipe") val recipeEntity: RecipeEntity? = null
)

@Serializable
data class Next(
    @SerialName("href") val href: String? = null,
    @SerialName("title") val title: String? = null
)

@Serializable
data class LinksXEntity(
    @SerialName("self") val self: Self? = null
)

@Serializable
data class RecipeEntity(
    @SerialName("calories") val calories: Double? = null,
    @SerialName("cautions") val cautions: List<String>? = null,
    @SerialName("cuisineType") val cuisineType: List<String>? = null,
    @SerialName("dietLabels") val dietLabels: List<String>? = null,
    @SerialName("digest") val digest: List<DigestEntity>? = null,
    @SerialName("dishType") val dishType: List<String>? = null,
    @SerialName("healthLabels") val healthLabels: List<String>? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("images") val images: ImagesEntity? = null,
    @SerialName("ingredientLines") val ingredientLines: List<String>? = null,
    @SerialName("ingredients") val ingredients: List<IngredientEntity>? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("mealType") val mealType: List<String>? = null,
    @SerialName("shareAs") val shareAs: String? = null,
    @SerialName("source") val source: String? = null,
    @SerialName("totalDaily") val totalDaily: TotalDailyEntity? = null,
    @SerialName("totalNutrients") val totalNutrients: TotalNutrientsEntity? = null,
    @SerialName("totalTime") val totalTime: Int? = null,
    @SerialName("totalWeight") val totalWeight: Double? = null,
    @SerialName("uri") val uri: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("yield") val yield: Int? = null
)

@Serializable
data class Self(
    @SerialName("href") val href: String? = null,
    @SerialName("title") val title: String? = null
)

@Serializable
data class DigestEntity(
    @SerialName("daily") val daily: Double? = null,
    @SerialName("hasRDI") val hasRDI: Boolean? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("schemaOrgTag") val schemaOrgTag: String? = null,
    @SerialName("sub") val sub: List<SubEntity>? = null,
    @SerialName("tag") val tag: String? = null,
    @SerialName("total") val total: Double? = null,
    @SerialName("unit") val unit: String? = null
)

@Serializable
data class ImagesEntity(
    @SerialName("LARGE") val large: LargeEntity? = null,
    @SerialName("REGULAR") val regular: RegularEntity? = null,
    @SerialName("SMALL") val small: SmallEntity? = null,
    @SerialName("THUMBNAIL") val thumbnail: ThumbnailEntity? = null
)

@Serializable
data class IngredientEntity(
    @SerialName("food") val food: String? = null,
    @SerialName("foodCategory") val foodCategory: String? = null,
    @SerialName("foodId") val foodId: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("measure") val measure: String? = null,
    @SerialName("quantity") val quantity: Int? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("weight") val weight: Double? = null
)

@Serializable
data class TotalDailyEntity(
    @SerialName("CA") val ca: Ca? = null,
    @SerialName("CHOCDF") val chocdf: Chocdf? = null,
    @SerialName("CHOLE") val chole: Chole? = null,
    @SerialName("ENERC_KCAL") val enercKcal: Enerckcal? = null,
    @SerialName("FASAT") val fasat: Fasat? = null,
    @SerialName("FAT") val fat: Fat? = null,
    @SerialName("FE") val fe: Fe? = null,
    @SerialName("FIBTG") val fibtg: Fibtg? = null,
    @SerialName("FOLDFE") val foldfe: Foldfe? = null,
    @SerialName("K") val k: K? = null,
    @SerialName("MG") val mg: MG? = null,
    @SerialName("NA") val na: NA? = null,
    @SerialName("NIA") val nia: Nia? = null,
    @SerialName("P") val p: P? = null,
    @SerialName("PROCNT") val procnt: Procnt? = null,
    @SerialName("RIBF") val ribf: Ribf? = null,
    @SerialName("THIA") val thia: Thia? = null,
    @SerialName("TOCPHA") val tocpha: Tocpha? = null,
    @SerialName("VITA_RAE") val vitaRae: Vitarae? = null,
    @SerialName("VITB12") val vitb12: Vitb12? = null,
    @SerialName("VITB6A") val vitb6a: Vitb6a? = null,
    @SerialName("VITC") val vitc: VitC? = null,
    @SerialName("VITD") val vitd: VitD? = null,
    @SerialName("VITK1") val vitk1: Vitk1? = null,
    @SerialName("ZN") val zn: ZN? = null
)

@Serializable
data class TotalNutrientsEntity(
    @SerialName("CA") val ca: CAX? = null,
    @SerialName("CHOCDF") val chocdf: CHOCDFX? = null,
    @SerialName("CHOLE") val chole: CHOLEX? = null,
    @SerialName("ENERC_KCAL") val enercKcal: ENERCKCALX? = null,
    @SerialName("FAMS") val fams: FAMS? = null,
    @SerialName("FAPU") val fapu: FAPU? = null,
    @SerialName("FASAT") val fasat: FASATX? = null,
    @SerialName("FAT") val fat: FATX? = null,
    @SerialName("FATRN") val fatrn: FATRN? = null,
    @SerialName("FE") val fe: FEX? = null,
    @SerialName("FIBTG") val fibtg: FIBTGX? = null,
    @SerialName("FOLAC") val folac: FOLAC? = null,
    @SerialName("FOLDFE") val foldfe: FOLDFEX? = null,
    @SerialName("FOLFD") val folfd: FOLFD? = null,
    @SerialName("K") val k: KX? = null,
    @SerialName("MG") val mg: MGX? = null,
    @SerialName("NA") val na: NAX? = null,
    @SerialName("NIA") val nia: NIAX? = null,
    @SerialName("P") val p: PX? = null,
    @SerialName("PROCNT") val procnt: PROCNTX? = null,
    @SerialName("RIBF") val ribf: RIBFX? = null,
    @SerialName("SUGAR") val sugar: SUGAR? = null,
    @SerialName("SUGAR.added") val sugarAdded: SUGARAdded? = null,
    @SerialName("THIA") val thia: THIAX? = null,
    @SerialName("TOCPHA") val tocpha: TOCPHAX? = null,
    @SerialName("VITA_RAE") val vitaRae: VITARAEX? = null,
    @SerialName("VITB12") val vitb12: VITB12X? = null,
    @SerialName("VITB6A") val vitb6a: VITB6AX? = null,
    @SerialName("VITC") val vitc: VITCX? = null,
    @SerialName("VITD") val vitd: VITDX? = null,
    @SerialName("VITK1") val vitk1: VITK1X? = null,
    @SerialName("WATER") val water: WATER? = null,
    @SerialName("ZN") val zn: ZNX? = null
)

@Serializable
data class SubEntity(
    @SerialName("daily") val daily: Double? = null,
    @SerialName("hasRDI") val hasRDI: Boolean? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("schemaOrgTag") val schemaOrgTag: String? = null,
    @SerialName("tag") val tag: String? = null,
    @SerialName("total") val total: Double? = null,
    @SerialName("unit") val unit: String? = null
)

@Serializable
data class LargeEntity(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)
@Serializable
data class RegularEntity(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)
@Serializable
data class SmallEntity(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)
@Serializable
data class ThumbnailEntity(
    val height: Int? = null,
    val url: String? = null,
    val width: Int? = null
)


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
)
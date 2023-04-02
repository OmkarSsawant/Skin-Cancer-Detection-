package te.mini_project.skincancerdetection.data

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("bkl")
	val bkl: Details,

	@field:SerializedName("df")
	val df: Details,

	@field:SerializedName("bcc")
	val bcc: Details,

	@field:SerializedName("akiec")
	val akiec: Details,

	@field:SerializedName("mel")
	val mel: Details,

	@field:SerializedName("nv")
	val nv: Details,

	@field:SerializedName("vasc")
	val vasc: Details
)

data class Details(

	@field:SerializedName("symptoms")
	val symptoms: String? = null,

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("danger_level")
	val dangerLevel: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("risk_factors")
	val riskFactors: String? = null,

	@field:SerializedName("prognosis")
	val prognosis: String? = null,

	@field:SerializedName("prevention")
	val prevention: String? = null
)

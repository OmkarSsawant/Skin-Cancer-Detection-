package te.mini_project.skincancerdetection.data

data class Result(val diseaseName:String,val accuracy:Float){
    constructor():this("",0f){

    }//for firestore
}

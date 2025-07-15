package hyperdimension.examples.iristsetlin;

/*"IDpol","ClaimNb","GLM_Pred","Tree_Pred","NN_Pred","PBM1_Pred","GLMBoost_Pred","PBM3_Pred","Boost_Pred","TotalSeverity","Severity"
*/

public record ModelComparison (

        int idPol,
        int ClaimNb,
        float GLM_Pred,
        float Tree_Pred,
        float NN_Pred,
        float PBM1_Pred,
        float GLMBoost_Pred,
        float PBM3_Pred,
        float Boost_Pred,
        float TM_Pred,
        float TotalSeverity,
        float Severity) {}



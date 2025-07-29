package hyperdimension.examples.iristsetlin;

/*"IDpol","ClaimNb","GLM_Pred","Tree_Pred","NN_Pred","PBM1_Pred","GLMBoost_Pred","PBM3_Pred","Boost_Pred","TotalSeverity","Severity"
*/

public class ModelComparison {

    private int idPol;
    private int ClaimNb;
    private float GLM_Pred;
    private float Tree_Pred;
    private float NN_Pred;
    private float PBM1_Pred;
    private float GLMBoost_Pred;
    private float PBM3_Pred;
    private float Boost_Pred;
    private float TM_Pred;
    private float TotalSeverity;
    private float Severity;

    public int getIdPol() {
        return idPol;
    }

    public void setIdPol(int idPol) {
        this.idPol = idPol;
    }

    public int getClaimNb() {
        return ClaimNb;
    }

    public void setClaimNb(int claimNb) {
        ClaimNb = claimNb;
    }

    public float getGLM_Pred() {
        return GLM_Pred;
    }

    public void setGLM_Pred(float GLM_Pred) {
        this.GLM_Pred = GLM_Pred;
    }

    public float getTree_Pred() {
        return Tree_Pred;
    }

    public void setTree_Pred(float tree_Pred) {
        Tree_Pred = tree_Pred;
    }

    public float getNN_Pred() {
        return NN_Pred;
    }

    public void setNN_Pred(float NN_Pred) {
        this.NN_Pred = NN_Pred;
    }

    public float getPBM1_Pred() {
        return PBM1_Pred;
    }

    public void setPBM1_Pred(float PBM1_Pred) {
        this.PBM1_Pred = PBM1_Pred;
    }

    public float getGLMBoost_Pred() {
        return GLMBoost_Pred;
    }

    public void setGLMBoost_Pred(float GLMBoost_Pred) {
        this.GLMBoost_Pred = GLMBoost_Pred;
    }

    public float getPBM3_Pred() {
        return PBM3_Pred;
    }

    public void setPBM3_Pred(float PBM3_Pred) {
        this.PBM3_Pred = PBM3_Pred;
    }

    public float getBoost_Pred() {
        return Boost_Pred;
    }

    public void setBoost_Pred(float boost_Pred) {
        Boost_Pred = boost_Pred;
    }

    public float getTM_Pred() {
        return TM_Pred;
    }

    public void setTM_Pred(float TM_Pred) {
        this.TM_Pred = TM_Pred;
    }

    public float getTotalSeverity() {
        return TotalSeverity;
    }

    public void setTotalSeverity(float totalSeverity) {
        TotalSeverity = totalSeverity;
    }

    public float getSeverity() {
        return Severity;
    }

    public void setSeverity(float severity) {
        Severity = severity;
    }

    /**
     * Constructor for ModelComparison.
     *
     * @param idPol            Policy ID
     * @param ClaimNb          Claim number
     * @param GLM_Pred         Prediction from GLM model
     * @param Tree_Pred        Prediction from Tree model
     * @param NN_Pred          Prediction from Neural Network model
     * @param PBM1_Pred        Prediction from PBM1 model
     * @param GLMBoost_Pred    Prediction from GLM Boost model
     * @param PBM3_Pred        Prediction from PBM3 model
     * @param Boost_Pred       Prediction from Boost model
     * @param TM_Pred          Prediction from TM model
     * @param TotalSeverity    Total severity of the claim
     * @param Severity         Severity of the claim
     */

    public ModelComparison(
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
        float Severity) {

        this.idPol = idPol;
        this.ClaimNb = ClaimNb;
        this.GLM_Pred = GLM_Pred;
        this.Tree_Pred = Tree_Pred;
        this.NN_Pred = NN_Pred;
        this.PBM1_Pred = PBM1_Pred;
        this.GLMBoost_Pred = GLMBoost_Pred;
        this.PBM3_Pred = PBM3_Pred;
        this.Boost_Pred = Boost_Pred;
        this.TM_Pred = TM_Pred;
        this.TotalSeverity = TotalSeverity;
        this.Severity = Severity;


    }

    /**
     * Override toString method to provide a string representation of the ModelComparison object.
     * Format all floats to #.#### format for better readability.
     * Only print the values as one line seperated with commas.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(idPol).append(",")
          .append(ClaimNb).append(",")
          .append(String.format("%.4f", GLM_Pred)).append(",")
          .append(String.format("%.4f", Tree_Pred)).append(",")
          .append(String.format("%.4f", NN_Pred)).append(",")
          .append(String.format("%.4f", PBM1_Pred)).append(",")
          .append(String.format("%.4f", GLMBoost_Pred)).append(",")
          .append(String.format("%.4f", PBM3_Pred)).append(",")
          .append(String.format("%.4f", Boost_Pred)).append(",")
          .append(String.format("%.4f", TM_Pred)).append(",")
          .append(String.format("%.4f", TotalSeverity)).append(",")
          .append(String.format("%.4f", Severity));

        return sb.toString();
    }


}



package ucr;

class UCResult {
    private int id;
    private String type;
    private String name;
    private int train;
    private int test;
    private int classCount;
    private int length;
    private double ed_w0;
    private double dtw_learned_w;
    private double dtw_w100;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrain() {
        return train;
    }

    public void setTrain(int train) {
        this.train = train;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    public int getClassCount() {
        return classCount;
    }

    public void setClassCount(int classCount) {
        this.classCount = classCount;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getEd_w0() {
        return ed_w0;
    }

    public void setEd_w0(double ed_w0) {
        this.ed_w0 = ed_w0;
    }

    public double getDtw_learned_w() {
        return dtw_learned_w;
    }

    public void setDtw_learned_w(double dtw_learned_w) {
        this.dtw_learned_w = dtw_learned_w;
    }

    public double getDtw_w100() {
        return dtw_w100;
    }

    public void setDtw_w100(double dtw_w100) {
        this.dtw_w100 = dtw_w100;
    }

    public double getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(double defaultRate) {
        this.defaultRate = defaultRate;
    }

    public String getDataDonorEditor() {
        return dataDonorEditor;
    }

    public void setDataDonorEditor(String dataDonorEditor) {
        this.dataDonorEditor = dataDonorEditor;
    }

    private double defaultRate;
    private String dataDonorEditor;

    public UCResult(int id, String type, String name, int train, int test, int classCount, int length,
                    double ed_w0, double dtw_learned_w, double dtw_w100, double defaultRate, String dataDonorEditor) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.train = train;
        this.test = test;
        this.classCount = classCount;
        this.length = length;
        this.ed_w0 = ed_w0;
        this.dtw_learned_w = dtw_learned_w;
        this.dtw_w100 = dtw_w100;
        this.defaultRate = defaultRate;
        this.dataDonorEditor = dataDonorEditor;
    }

    @Override
    public String toString() {
        return "UCResult{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", train=" + train +
                ", test=" + test +
                ", classCount=" + classCount +
                ", length=" + length +
                ", ed_w0=" + ed_w0 +
                ", dtw_learned_w=" + dtw_learned_w +
                ", dtw_w100=" + dtw_w100 +
                ", defaultRate=" + defaultRate +
                ", dataDonorEditor='" + dataDonorEditor + '\'' +
                '}';
    }
}
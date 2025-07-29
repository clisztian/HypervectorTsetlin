package hyperdimension.examples.claims;

public class MotorClaim {
    private int IDpol;
    private int ClaimNb;
    private double Exposure;
    private String Area;
    private int VehPower;
    private int VehAge;
    private int DrivAge;
    private int BonusMalus;
    private String VehBrand;
    private String VehGas;
    private int Density;
    private String Region;

    public MotorClaim(String[] fields) {
        this.IDpol = (int) Double.parseDouble(fields[0].trim());
        this.ClaimNb = Integer.parseInt(fields[1].trim());
        this.Exposure = Double.parseDouble(fields[2].trim());
        this.Area = fields[3].trim();
        this.VehPower = Integer.parseInt(fields[4].trim());
        this.VehAge = Integer.parseInt(fields[5].trim());
        this.DrivAge = Integer.parseInt(fields[6].trim());
        this.BonusMalus = Integer.parseInt(fields[7].trim());
        this.VehBrand = fields[8].trim();
        this.VehGas = fields[9].trim();
        this.Density = (int) Double.parseDouble(fields[10].trim());
        this.Region = fields[11].trim();
    }

    public MotorClaim() {
        // Default constructor
    }

    public int getIDpol() {
        return IDpol;
    }

    public void setIDpol(int IDpol) {
        this.IDpol = IDpol;
    }

    public int getClaimNb() {
        return ClaimNb;
    }

    public void setClaimNb(int claimNb) {
        ClaimNb = claimNb;
    }

    public double getExposure() {
        return Exposure;
    }

    public void setExposure(double exposure) {
        Exposure = exposure;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public int getVehPower() {
        return VehPower;
    }

    public void setVehPower(int vehPower) {
        VehPower = vehPower;
    }

    public int getVehAge() {
        return VehAge;
    }

    public void setVehAge(int vehAge) {
        VehAge = vehAge;
    }

    public int getDrivAge() {
        return DrivAge;
    }

    public void setDrivAge(int drivAge) {
        DrivAge = drivAge;
    }

    public int getBonusMalus() {
        return BonusMalus;
    }

    public void setBonusMalus(int bonusMalus) {
        BonusMalus = bonusMalus;
    }

    public String getVehBrand() {
        return VehBrand;
    }

    public void setVehBrand(String vehBrand) {
        VehBrand = vehBrand;
    }

    public String getVehGas() {
        return VehGas;
    }

    public void setVehGas(String vehGas) {
        VehGas = vehGas;
    }

    public int getDensity() {
        return Density;
    }

    public void setDensity(int density) {
        Density = density;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }


    //create a copy of the MotorClaim object
    public MotorClaim falseCopy() {
        MotorClaim copy = new MotorClaim();
        copy.IDpol = this.IDpol;
        copy.Exposure = this.Exposure;
        copy.Area = this.Area;
        copy.VehPower = this.VehPower;
        copy.VehAge = this.VehAge;
        copy.DrivAge = this.DrivAge;
        copy.BonusMalus = this.BonusMalus;
        copy.VehBrand = this.VehBrand;
        copy.VehGas = this.VehGas;
        copy.Density = this.Density;
        copy.Region = this.Region;

        if(this.ClaimNb == 0) {
            copy.ClaimNb = 1; // Ensure ClaimNb is at least 1
        }
        else if(this.ClaimNb == 1) {
            copy.ClaimNb = 0; // Ensure ClaimNb is at most 1
        }

        return copy;
    }

    @Override
    public String toString() {
        return "MotorClaim{" +
                "IDpol=" + IDpol +
                ", ClaimNb=" + ClaimNb +
                ", Exposure=" + Exposure +
                ", Area='" + Area + '\'' +
                ", VehPower=" + VehPower +
                ", VehAge=" + VehAge +
                ", DrivAge=" + DrivAge +
                ", BonusMalus=" + BonusMalus +
                ", VehBrand='" + VehBrand + '\'' +
                ", VehGas='" + VehGas + '\'' +
                ", Density=" + Density +
                ", Region='" + Region + '\'' +
                '}';
    }


    // Getters and setters (or use Lombok @Data for brevity)
}



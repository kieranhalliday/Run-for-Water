package yukon.runforlife;

/**
 * Created by Kieran Halliday on 2017-11-04
 */

// This class is required in order to parse Firebase objects properly
    // It follows the format required for easy deserialization
    // The static builder class exists to avoid a large number of constructors, getters and setters
public class InformationWell {
    private String name, cdnGroup, streetAddress, town, county, typeOfProperty, localTerrain, locationRelativeToSlope,
            contactName, contactCell, contactEmail, drillingStartDate, drillingFinishDate, pumpInstallationDate,
            commentsOnDrillingAndPump, wellId, numberOfDaysDrilling, averageDrillingPerDayInMeters, depth, depthToWater, depthOfPumpIntake,
            depthToBedrock, depthDrilledIntoBedRock, numberOfNearbyWells, idOfNearbyWells, wellWaterColumn,
            drySeasonWaterTableDepth, wetSeasonWaterTableDepth, drySeasonFlowRate, wetSeasonFlowRate;

    private Double wellLatitude, wellLongitude;

    private boolean rockBitUsed, historical;

    public InformationWell() {
    }

    private InformationWell(InfoWellBuilder infoWellBuilder) {
        this.name = infoWellBuilder.name;
        this.cdnGroup = infoWellBuilder.cdnGroup;
        this.streetAddress = infoWellBuilder.streetAddress;
        this.town = infoWellBuilder.town;
        this.county = infoWellBuilder.county;
        this.typeOfProperty = infoWellBuilder.typeOfProperty;
        this.localTerrain = infoWellBuilder.localTerrain;
        this.locationRelativeToSlope = infoWellBuilder.locationRelativeToSlope;
        this.contactName = infoWellBuilder.contactName;
        this.contactCell = infoWellBuilder.contactCell;
        this.contactEmail = infoWellBuilder.contactEmail;
        this.drillingStartDate = infoWellBuilder.drillingStartDate;
        this.drillingFinishDate = infoWellBuilder.drillingFinishDate;
        this.pumpInstallationDate = infoWellBuilder.pumpInstallationDate;
        this.commentsOnDrillingAndPump = infoWellBuilder.commentsOnDrillingAndPump;
        this.wellId = infoWellBuilder.wellId;
        this.numberOfDaysDrilling = infoWellBuilder.numberOfDaysDrilling;
        this.averageDrillingPerDayInMeters = infoWellBuilder.averageDrillingPerDayInMeters;
        this.depth = infoWellBuilder.depth;
        this.depthToWater = infoWellBuilder.depthToWater;
        this.depthOfPumpIntake = infoWellBuilder.depthOfPumpIntake;
        this.depthToBedrock = infoWellBuilder.depthToBedrock;
        this.depthDrilledIntoBedRock = infoWellBuilder.depthDrilledIntoBedRock;
        this.numberOfNearbyWells = infoWellBuilder.numberOfNearbyWells;
        this.idOfNearbyWells = infoWellBuilder.idOfNearbyWells;
        this.wellLatitude = infoWellBuilder.wellLatitude;
        this.wellLongitude = infoWellBuilder.wellLongitude;
        this.wellWaterColumn = infoWellBuilder.wellWaterColumn;
        this.rockBitUsed = infoWellBuilder.rockBitUsed;
        this.historical = infoWellBuilder.historical;
        this.drySeasonFlowRate = infoWellBuilder.drySeasonFlowRate;
        this.drySeasonWaterTableDepth = infoWellBuilder.drySeasonWaterTableDepth;
        this.wetSeasonFlowRate = infoWellBuilder.wetSeasonFlowRate;
        this.wetSeasonWaterTableDepth = infoWellBuilder.wetSeasonWaterTableDepth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCdnGroup() {
        return cdnGroup;
    }

    public void setCdnGroup(String cdnGroup) {
        this.cdnGroup = cdnGroup;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTypeOfProperty() {
        return typeOfProperty;
    }

    public void setTypeOfProperty(String typeOfProperty) {
        this.typeOfProperty = typeOfProperty;
    }

    public String getLocalTerrain() {
        return localTerrain;
    }

    public void setLocalTerrain(String localTerrain) {
        this.localTerrain = localTerrain;
    }

    public String getLocationRelativeToSlope() {
        return locationRelativeToSlope;
    }

    public void setLocationRelativeToSlope(String locationRelativeToSlope) {
        this.locationRelativeToSlope = locationRelativeToSlope;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactCell() {
        return contactCell;
    }

    public void setContactCell(String contactCell) {
        this.contactCell = contactCell;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDrillingStartDate() {
        return drillingStartDate;
    }

    public void setDrillingStartDate(String drillingStartDate) {
        this.drillingStartDate = drillingStartDate;
    }

    public String getDrillingFinishDate() {
        return drillingFinishDate;
    }

    public void setDrillingFinishDate(String drillingFinishDate) {
        this.drillingFinishDate = drillingFinishDate;
    }

    public String getPumpInstallationDate() {
        return pumpInstallationDate;
    }

    public void setPumpInstallationDate(String pumpInstallationDate) {
        this.pumpInstallationDate = pumpInstallationDate;
    }

    public String getCommentsOnDrillingAndPump() {
        return commentsOnDrillingAndPump;
    }

    public void setCommentsOnDrillingAndPump(String commentsOnDrillingAndPump) {
        this.commentsOnDrillingAndPump = commentsOnDrillingAndPump;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getNumberOfDaysDrilling() {
        return numberOfDaysDrilling;
    }

    public void setNumberOfDaysDrilling(String numberOfDaysDrilling) {
        this.numberOfDaysDrilling = numberOfDaysDrilling;
    }

    public String getAverageDrillingPerDayInMeters() {
        return averageDrillingPerDayInMeters;
    }

    public void setAverageDrillingPerDayInMeters(String averageDrillingPerDayInMeters) {
        this.averageDrillingPerDayInMeters = averageDrillingPerDayInMeters;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDepthToWater() {
        return depthToWater;
    }

    public void setDepthToWater(String depthToWater) {
        this.depthToWater = depthToWater;
    }

    public String getDepthOfPumpIntake() {
        return depthOfPumpIntake;
    }

    public void setDepthOfPumpIntake(String depthOfPumpIntake) {
        this.depthOfPumpIntake = depthOfPumpIntake;
    }

    public String getDepthToBedrock() {
        return depthToBedrock;
    }

    public void setDepthToBedrock(String depthToBedrock) {
        this.depthToBedrock = depthToBedrock;
    }

    public String getDepthDrilledIntoBedRock() {
        return depthDrilledIntoBedRock;
    }

    public void setDepthDrilledIntoBedRock(String depthDrilledIntoBedRock) {
        this.depthDrilledIntoBedRock = depthDrilledIntoBedRock;
    }

    public String getNumberOfNearbyWells() {
        return numberOfNearbyWells;
    }

    public void setNumberOfNearbyWells(String numberOfNearbyWells) {
        this.numberOfNearbyWells = numberOfNearbyWells;
    }

    public String getIdOfNearbyWells() {
        return idOfNearbyWells;
    }

    public void setIdOfNearbyWells(String idOfNearbyWells) {
        this.idOfNearbyWells = idOfNearbyWells;
    }

    public double getWellLatitude() {
        return wellLatitude;
    }

    public void setWellLatitude(double wellLatitude) {
        this.wellLatitude = wellLatitude;
    }

    public double getWellLongitude() {
        return wellLongitude;
    }

    public void setWellLongitude(double wellLongitude) {
        this.wellLongitude = wellLongitude;
    }

    public String getWellWaterColumn() {
        return wellWaterColumn;
    }

    public void setWellWaterColumn(String wellWaterColumn) {
        this.wellWaterColumn = wellWaterColumn;
    }

    public boolean isRockBitUsed() {
        return rockBitUsed;
    }

    public void setRockBitUsed(boolean rockBitUsed) {
        this.rockBitUsed = rockBitUsed;
    }

    public boolean getHistorical() {
        return historical;
    }

    public void setHistorical(boolean historical) {
        this.historical = historical;
    }

    public String getDrySeasonWaterTableDepth() {
        return drySeasonWaterTableDepth;
    }

    public void setDrySeasonWaterTableDepth(String drySeasonWaterTableDepth) {
        this.drySeasonWaterTableDepth = drySeasonWaterTableDepth;
    }

    public String getWetSeasonWaterTableDepth() {
        return wetSeasonWaterTableDepth;
    }

    public void setWetSeasonWaterTableDepth(String wetSeasonWaterTableDepth) {
        this.wetSeasonWaterTableDepth = wetSeasonWaterTableDepth;
    }

    public String getDrySeasonFlowRate() {
        return drySeasonFlowRate;
    }

    public void setDrySeasonFlowRate(String drySeasonFlowRate) {
        this.drySeasonFlowRate = drySeasonFlowRate;
    }

    public String getWetSeasonFlowRate() {
        return wetSeasonFlowRate;
    }

    public void setWetSeasonFlowRate(String wetSeasonFlowRate) {
        this.wetSeasonFlowRate = wetSeasonFlowRate;
    }

    static class InfoWellBuilder {
        private final String contactEmail;

        private final String wellId;

        private String cdnGroup, county, typeOfProperty, localTerrain, locationRelativeToSlope,
                contactName, contactCell, drillingStartDate, drillingFinishDate, pumpInstallationDate,
                commentsOnDrillingAndPump, numberOfDaysDrilling, averageDrillingPerDayInMeters, depth, depthToWater, depthOfPumpIntake,
                depthToBedrock, depthDrilledIntoBedRock, numberOfNearbyWells,
                idOfNearbyWells, wellWaterColumn, name, streetAddress, town,
                drySeasonWaterTableDepth, wetSeasonWaterTableDepth, drySeasonFlowRate, wetSeasonFlowRate;

        private final double wellLatitude, wellLongitude;

        private boolean rockBitUsed, historical;

        public InfoWellBuilder setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public InfoWellBuilder setCdnGroup(String cdnGroup) {
            this.cdnGroup = cdnGroup;
            return this;
        }

        public InfoWellBuilder setCounty(String county) {
            this.county = county;
            return this;
        }

        public InfoWellBuilder setTypeOfProperty(String typeOfProperty) {
            this.typeOfProperty = typeOfProperty;
            return this;
        }

        public InfoWellBuilder setLocalTerrain(String localTerrain) {
            this.localTerrain = localTerrain;
            return this;
        }

        public InfoWellBuilder setLocationRelativeToSlope(String locationRelativeToSlope) {
            this.locationRelativeToSlope = locationRelativeToSlope;
            return this;
        }

        public InfoWellBuilder setContactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public InfoWellBuilder setContactCell(String contactCell) {
            this.contactCell = contactCell;
            return this;
        }

        public InfoWellBuilder setDrillingStartDate(String drillingStartDate) {
            this.drillingStartDate = drillingStartDate;
            return this;
        }

        public InfoWellBuilder setDrillingFinishDate(String drillingFinishDate) {
            this.drillingFinishDate = drillingFinishDate;
            return this;
        }

        public InfoWellBuilder setPumpInstallationDate(String pumpInstallationDate) {
            this.pumpInstallationDate = pumpInstallationDate;
            return this;
        }

        public InfoWellBuilder setCommentsOnDrillingAndPump(String commentsOnDrillingAndPump) {
            this.commentsOnDrillingAndPump = commentsOnDrillingAndPump;
            return this;
        }

        public InfoWellBuilder setNumberOfDaysDrilling(String numberOfDaysDrilling) {
            this.numberOfDaysDrilling = numberOfDaysDrilling;
            return this;
        }

        public InfoWellBuilder setAverageDrillingPerDayInMeters(String averageDrillingPerDayInMeters) {
            this.averageDrillingPerDayInMeters = averageDrillingPerDayInMeters;
            return this;
        }

        public InfoWellBuilder setDepth(String depth) {
            this.depth = depth;
            return this;
        }

        public InfoWellBuilder setDepthToWater(String depthToWater) {
            this.depthToWater = depthToWater;
            return this;
        }

        public InfoWellBuilder setDepthOfPumpIntake(String depthOfPumpIntake) {
            this.depthOfPumpIntake = depthOfPumpIntake;
            return this;
        }

        public InfoWellBuilder setDepthToBedrock(String depthToBedrock) {
            this.depthToBedrock = depthToBedrock;
            return this;
        }

        public InfoWellBuilder setDepthDrilledIntoBedRock(String depthDrilledIntoBedRock) {
            this.depthDrilledIntoBedRock = depthDrilledIntoBedRock;
            return this;
        }

        public InfoWellBuilder setNumberOfNearbyWells(String numberOfNearbyWells) {
            this.numberOfNearbyWells = numberOfNearbyWells;
            return this;
        }

        public InfoWellBuilder setIdOfNearbyWells(String idOfNearbyWells) {
            this.idOfNearbyWells = idOfNearbyWells;
            return this;
        }

        public InfoWellBuilder setWellWaterColumn(String wellWaterColumn) {
            this.wellWaterColumn = wellWaterColumn;
            return this;
        }

        public InfoWellBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public InfoWellBuilder setTown(String town) {
            this.town = town;
            return this;
        }

        public InfoWellBuilder setRockBitUsed(boolean rockBitUsed) {
            this.rockBitUsed = rockBitUsed;
            return this;
        }


        InfoWellBuilder(String contactEmail, double latitude, double wellLongitude, String wellId) {
            this.contactEmail = contactEmail;
            this.wellLatitude = latitude;
            this.wellLongitude = wellLongitude;
            this.wellId = wellId;

        }

        InformationWell build() {
            return new InformationWell(this);
        }


        public void setHistorical(boolean historical) {
            this.historical = historical;
        }

        public String getDrySeasonWaterTableDepth() {
            return drySeasonWaterTableDepth;
        }

        public void setDrySeasonWaterTableDepth(String drySeasonWaterTableDepth) {
            this.drySeasonWaterTableDepth = drySeasonWaterTableDepth;
        }

        public String getWetSeasonWaterTableDepth() {
            return wetSeasonWaterTableDepth;
        }

        public void setWetSeasonWaterTableDepth(String wetSeasonWaterTableDepth) {
            this.wetSeasonWaterTableDepth = wetSeasonWaterTableDepth;
        }

        public String getDrySeasonFlowRate() {
            return drySeasonFlowRate;
        }

        public void setDrySeasonFlowRate(String drySeasonFlowRate) {
            this.drySeasonFlowRate = drySeasonFlowRate;
        }

        public String getWetSeasonFlowRate() {
            return wetSeasonFlowRate;
        }

        public void setWetSeasonFlowRate(String wetSeasonFlowRate) {
            this.wetSeasonFlowRate = wetSeasonFlowRate;
        }
    }
}

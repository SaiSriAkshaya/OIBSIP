/** Holds the details of one ticket booking. */
public class Reservation {
    private long pnr;
    private final String passengerName;
    private final int trainNo;
    private final String trainName;
    private final String classType;
    private final String journeyDate;
    private final String source;
    private final String destination;

    public Reservation(long pnr, String passengerName, int trainNo, String trainName,
                       String classType, String journeyDate, String source, String destination) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNo = trainNo;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.source = source;
        this.destination = destination;
    }

    public long getPnr() { return pnr; }
    public void setPnr(long pnr) { this.pnr = pnr; }
    public String getPassengerName() { return passengerName; }
    public int getTrainNo() { return trainNo; }
    public String getTrainName() { return trainName; }
    public String getClassType() { return classType; }
    public String getJourneyDate() { return journeyDate; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }

    public String toDetails() {
        return "PNR Number     : " + pnr + "\n"
             + "Passenger      : " + passengerName + "\n"
             + "Train          : " + trainNo + " - " + trainName + "\n"
             + "Class          : " + classType + "\n"
             + "Date of Journey: " + journeyDate + "\n"
             + "From           : " + source + "\n"
             + "To             : " + destination;
    }
}

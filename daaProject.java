import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

class Resource {
    String name;
    int availableTime; // Time when the resource is next available

    Resource(String name) {
        this.name = name;
        this.availableTime = 0;
    }
}

class Patient {
    String name;
    int arrivalTime;
    int treatmentDuration;
    int id; // Unique identifier for DP

    Patient(String name, int arrivalTime, int treatmentDuration, int id) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.treatmentDuration = treatmentDuration;
        this.id = id;
    }
}

class Appointment {
    Patient patient;
    Resource doctor;
    Resource room;
    int startTime;
    int endTime;

    Appointment(Patient patient, Resource doctor, Resource room, int startTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.room = room;
        this.startTime = startTime;
        this.endTime = startTime + patient.treatmentDuration;
    }

    @Override
    public String toString() {
        return String.format("Patient: %s, Doctor: %s, Room: %s, Start Time: %d, End Time: %d",
                patient.name, doctor.name, room.name, startTime, endTime);
    }
}

public class daaProject {

    public static void main(String[] args) {
        // Define resources
        List<Resource> doctors = new ArrayList<>();
        List<Resource> rooms = new ArrayList<>();
        doctors.add(new Resource("Dr. Smith"));
        doctors.add(new Resource("Dr. Johnson"));
        rooms.add(new Resource("Room 1"));
        rooms.add(new Resource("Room 2"));

        // Define patients
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("Alice", 0, 30, 0));
        patients.add(new Patient("Bob", 15, 45, 1));
        patients.add(new Patient("Charlie", 30, 30, 2));

        // Schedule appointments with greedy approach
        List<Appointment> appointments = greedyScheduleAppointments(patients, doctors, rooms);

        // Output appointments
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }

        // Optimize the schedule with dynamic programming
        List<Appointment> optimizedAppointments = optimizeScheduleWithDP(appointments);

        // Output optimized appointments
        System.out.println("\nOptimized Schedule:");
        for (Appointment appointment : optimizedAppointments) {
            System.out.println(appointment);
        }
    }

    public static List<Appointment> (List<Patient> patients, List<Resource> doctors, List<Resource> rooms) {
        List<Appointment> appointments = new ArrayList<>();
        PriorityQueue<Patient> patientQueue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));
        patientQueue.addAll(patients);

        while (!patientQueue.isEmpty()) {
            Patient patient = patientQueue.poll();
            Resource availableDoctor = findAvailableResource(doctors, patient.arrivalTime);
            Resource availableRoom = findAvailableResource(rooms, patient.arrivalTime);

            if (availableDoctor != null && availableRoom != null) {
                availableDoctor.availableTime = patient.arrivalTime + patient.treatmentDuration;
                availableRoom.availableTime = patient.arrivalTime + patient.treatmentDuration;
                appointments.add(new Appointment(patient, availableDoctor, availableRoom, patient.arrivalTime));
            } else {
                System.out.println("No available resources for patient: " + patient.name);
            }
        }

        return appointments;
    }

    private static Resource findAvailableResource(List<Resource> resources, int currentTime) {
        Resource bestResource = null;
        int earliestAvailableTime = Integer.MAX_VALUE;

        for (Resource resource : resources) {
            if (resource.availableTime <= currentTime && resource.availableTime < earliestAvailableTime) {
                earliestAvailableTime = resource.availableTime;
                bestResource = resource;
            }
        }

        return bestResource;
    }

    // Dynamic Programming optimization
    public static List<Appointment> optimizeScheduleWithDP(List<Appointment> appointments) {
        // Sort appointments by end time for DP approach
        appointments.sort((a1, a2) -> Integer.compare(a1.endTime, a2.endTime));

        int n = appointments.size();
        int[] dp = new int[n];
        int[] prev = new int[n];
        
        Arrays.fill(dp, 0);
        Arrays.fill(prev, -1);

        for (int i = 0; i < n; i++) {
            dp[i] = appointments.get(i).endTime - appointments.get(i).startTime;
            prev[i] = -1;

            for (int j = 0; j < i; j++) {
                if (appointments.get(j).endTime <= appointments.get(i).startTime) {
                    if (dp[i] < dp[j] + (appointments.get(i).endTime - appointments.get(i).startTime)) {
                        dp[i] = dp[j] + (appointments.get(i).endTime - appointments.get(i).startTime);
                        prev[i] = j;
                    }
                }
            }
        }

        // Backtrack to find the optimal schedule
        List<Appointment> optimizedAppointments = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (dp[i] > dp[idx]) {
                idx = i;
            }
        }

        while (idx != -1) {
            optimizedAppointments.add(appointments.get(idx));
            idx = prev[idx];
        }

        return optimizedAppointments;
    }
}
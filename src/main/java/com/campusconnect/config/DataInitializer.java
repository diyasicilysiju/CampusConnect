package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.RegistrationRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           EventRepository eventRepository,
                           RegistrationRepository registrationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedDatabase();
        }
    }

    @Transactional
    public void resetData() {
        registrationRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        registrationRepository.flush();
        eventRepository.flush();
        userRepository.flush();
        seedDatabase();
    }

    @Transactional
    public void seedDatabase() {
        // 1. Seed Users
        User admin = new User(
                "System Administrator",
                "ADMIN-001",
                "admin@campusconnect.edu",
                passwordEncoder.encode("Admin@123"),
                Role.ADMIN
        );
        userRepository.save(admin);

        User student1 = new User(
                "John Doe",
                "STU-2024-001",
                "john.doe@campusconnect.edu",
                passwordEncoder.encode("Student@123"),
                Role.STUDENT
        );
        userRepository.save(student1);

        User student2 = new User(
                "Jane Smith",
                "STU-2024-002",
                "jane.smith@campusconnect.edu",
                passwordEncoder.encode("Student@123"),
                Role.STUDENT
        );
        userRepository.save(student2);

        User student3 = new User(
                "Alex Kumar",
                "STU-2024-003",
                "alex.kumar@campusconnect.edu",
                passwordEncoder.encode("Student@123"),
                Role.STUDENT
        );
        userRepository.save(student3);

        LocalDate today = LocalDate.now();

        // 2. Seed Events (Dynamic future & past dates)
        Event e1 = new Event(
                "CodeFest 2026",
                "Annual 24-hour university hackathon featuring algorithm challenges, mobile app development, and AI tracks.",
                "Technical",
                today.plusDays(14),
                LocalTime.of(9, 30),
                "Main Auditorium & Computing Labs",
                "Department of Computer Science",
                100,
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e1);

        Event e2 = new Event(
                "AI and Machine Learning Workshop",
                "Hands-on workshop exploring deep learning neural networks, computer vision, and prompt engineering with PyTorch.",
                "Workshops",
                today.plusDays(20),
                LocalTime.of(10, 0),
                "Tech Hub Seminar Hall B",
                "AI Research Lab & Student Chapter",
                50,
                "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e2);

        Event e3 = new Event(
                "Annual Cultural Fest",
                "Three days of non-stop dance, live band performances, theatrical drama, music showdowns, and food carnivals.",
                "Cultural",
                today.plusDays(35),
                LocalTime.of(16, 0),
                "University Open Air Amphitheatre",
                "Student Cultural Affairs Committee",
                300,
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e3);

        Event e4 = new Event(
                "Inter-College Sports Meet",
                "Track events, football championship, basketball playoffs, badminton singles/doubles, and table tennis matches.",
                "Sports",
                today.plusDays(25),
                LocalTime.of(8, 0),
                "Campus Sports Complex & Stadium",
                "Department of Physical Education",
                200,
                "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e4);

        Event e5 = new Event(
                "Robotics Challenge",
                "Line follower robot race, maze solving challenge, and combat robot war showcasing autonomous engineering designs.",
                "Technical",
                today.plusDays(18),
                LocalTime.of(11, 0),
                "Mechanical Engineering Workshop Bay",
                "Robotics & Automation Society",
                40,
                "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e5);

        Event e6 = new Event(
                "Cybersecurity Awareness Seminar",
                "Industry keynote on ethical hacking, zero trust architecture, threat intelligence, and digital privacy safety.",
                "Workshops",
                today.plusDays(28),
                LocalTime.of(14, 0),
                "E-Classroom 101, Science Block",
                "Information Security Club",
                80,
                "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e6);

        // Event e7 is intentionally capacity 2 with 2 registrations to test FULL EVENT scenario!
        Event e7 = new Event(
                "Photography Competition",
                "Campus life visual storytelling contest. Limited slots available for jury portfolio evaluation.",
                "Cultural",
                today.plusDays(12),
                LocalTime.of(13, 0),
                "Fine Arts Gallery & Studio",
                "Visual Arts & Media Guild",
                2,
                "https://images.unsplash.com/photo-1452587925148-ce544e77e70d?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e7);

        Event e8 = new Event(
                "Research and Innovation Symposium",
                "Undergraduate and postgraduate research poster presentations, faculty peer reviews, and guest tech talks.",
                "Technical",
                today.plusDays(40),
                LocalTime.of(10, 30),
                "Research Park Convention Center",
                "Office of Academic Research",
                60,
                "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e8);

        // Event e9 is intentionally a PAST EVENT to test PAST EVENT scenario!
        Event e9 = new Event(
                "Campus Orientation & Induction 2025",
                "Annual orientation session for newly admitted batch outlining academic policies and campus facilities.",
                "Workshops",
                today.minusDays(30),
                LocalTime.of(9, 0),
                "University Hall",
                "Student Affairs Division",
                150,
                "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600&auto=format&fit=crop&q=80"
        );
        eventRepository.save(e9);

        // 3. Seed Initial Registrations
        // e1: John Doe registered
        registrationRepository.save(new Registration(student1, e1, RegistrationStatus.REGISTERED));

        // e3: Jane Smith registered
        registrationRepository.save(new Registration(student2, e3, RegistrationStatus.REGISTERED));

        // e7: Both student1 and student2 registered (Capacity 2 / Registered 2 -> Full Event!)
        registrationRepository.save(new Registration(student1, e7, RegistrationStatus.REGISTERED));
        registrationRepository.save(new Registration(student2, e7, RegistrationStatus.REGISTERED));
    }
}

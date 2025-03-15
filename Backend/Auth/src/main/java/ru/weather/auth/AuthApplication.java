package ru.weather.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@SpringBootApplication
public class AuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	/* Пример как написать скрипт для взаимодейсвия с БД
	public static void main(String[] args) {
		// Настройка подключения к БД
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/weather");
		dataSource.setUsername("postgres");
		dataSource.setPassword("root");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		// Данные пользователей
		Object[][] users = {
				{"Иванов Иван Иванович", "ivanov@example.com", "ivanovii", "qwerty123"},
				{"Петров Петр Петрович", "petrov@example.com", "petrovpp", "qwerty123"},
				{"Сидоров Алексей Николаевич", "sidorov@example.com", "sidorovan", "qwerty123"},
				{"Кузнецов Дмитрий Сергеевич", "kuznetsov@example.com", "kuznetsovds", "qwerty123"},
				{"Смирнов Олег Викторович", "smirnov@example.com", "smirnovov", "qwerty123"}
		};


		for (Object[] user : users) {
			String hashedPassword = passwordEncoder.encode(user[3].toString());
			String sql = "INSERT INTO users (id, username, email, login, password) VALUES (?, ?, ?, ?, ?)";
			jdbcTemplate.update(sql, UUID.randomUUID(), user[0], user[1], user[2], hashedPassword);
		}

		System.out.println("Пользователи успешно добавлены!");
	}
	*/
}

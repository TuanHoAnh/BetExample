-- password: Admin@123
INSERT INTO users(username, password, email, first_name, last_name, language_tag, activated, role, created_at, created_by)
  VALUES ('admin', '$2a$10$NiK.3NQeblayPGlcmHM70uOBoBGeRxC8xa86XydNTHREjO5WTc5uC', 'hoanhtuan81pk@gmail.com',
          'Administrator', 'User', 'en', true, 'ADMIN', now(), 'SYSTEM');

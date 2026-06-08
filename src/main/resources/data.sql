-- Роли
INSERT INTO roles(name) VALUES ('ROLE_CLIENT');
INSERT INTO roles(name) VALUES ('ROLE_MARKETING');
INSERT INTO roles(name) VALUES ('ROLE_ADMIN');
INSERT INTO roles(name) VALUES ('ROLE_MARKETING_ANALYST');

-- Пользователи (пароли BCrypt)
-- admin123, marketing123, analyst123, client123
INSERT INTO users(username, email, password, bonus_balance, loyalty_status, consent_given, created_at)
VALUES ('admin', 'admin@sks.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, 'VIP', true, NOW());

INSERT INTO users(username, email, password, bonus_balance, loyalty_status, consent_given, created_at)
VALUES ('marketing', 'marketing@sks.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, 'REGULAR', true, NOW());

INSERT INTO users(username, email, password, bonus_balance, loyalty_status, consent_given, created_at)
VALUES ('analyst', 'analyst@sks.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, 'REGULAR', true, NOW());

INSERT INTO users(username, email, password, bonus_balance, loyalty_status, consent_given, created_at)
VALUES ('client', 'client@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 500, 'REGULAR', true, NOW());

-- Привязка ролей
INSERT INTO user_roles(user_id, role_id) VALUES (1, 3); -- admin -> ADMIN
INSERT INTO user_roles(user_id, role_id) VALUES (2, 2); -- marketing -> MARKETING
INSERT INTO user_roles(user_id, role_id) VALUES (3, 4); -- analyst -> ANALYST
INSERT INTO user_roles(user_id, role_id) VALUES (4, 1); -- client -> CLIENT

-- Достижения
INSERT INTO achievements(name, description, icon, condition_type, threshold, reward_amount) VALUES
                                                                                                ('Первый займ', 'Оформите первый займ', '💰', 'LOAN_COUNT', 1, 100),
                                                                                                ('Пятый займ', 'Оформите 5 займов', '🏆', 'LOAN_COUNT', 5, 500),
                                                                                                ('Год с нами', 'Год в программе лояльности', '🎂', 'MEMBERSHIP_DAYS', 365, 1000),
                                                                                                ('1000 бонусов', 'Накопите 1000 бонусов', '💎', 'BONUS_BALANCE', 1000, 200),
                                                                                                ('10000 бонусов', 'Накопите 10000 бонусов', '👑', 'BONUS_BALANCE', 10000, 2000),
                                                                                                ('Без просрочек', 'Выкуп залога без просрочки', '✅', 'PERFECT_PAYMENT', 1, 300),
                                                                                                ('Ранняя пташка', 'Визит в офис до 10:00', '🌅', 'EARLY_VISIT', 1, 50);

-- Призы каталога
INSERT INTO prizes(name, description, category, cost, stock, image_url, active) VALUES
                                                                                    ('Скидка 5% на следующий займ', 'Скидка на проценты по следующему ЗБ', 'FINANCIAL', 500, 100, '/img/discount5.png', true),
                                                                                    ('Скидка 10% на следующий займ', 'Скидка на проценты по следующему ЗБ', 'FINANCIAL', 1000, 50, '/img/discount10.png', true),
                                                                                    ('Бесплатное хранение 7 дней', 'Бесплатное хранение залога', 'FINANCIAL', 300, 200, '/img/storage.png', true),
                                                                                    ('Сертификат Wildberries 500₽', 'Электронный сертификат', 'PARTNER', 450, 100, '/img/wb.png', true),
                                                                                    ('Сертификат OZON 1000₽', 'Электронный сертификат', 'PARTNER', 900, 50, '/img/ozon.png', true),
                                                                                    ('Футболка SKS', 'Фирменный мерч', 'MERCH', 1500, 30, '/img/tshirt.png', true),
                                                                                    ('Термокружка SKS', 'Фирменный мерч', 'MERCH', 800, 50, '/img/mug.png', true),
                                                                                    ('Благотворительный взнос 100₽', 'Перевод в фонд помощи', 'CHARITY', 100, 9999, '/img/charity.png', true),
                                                                                    ('Встреча с CEO', 'Эксклюзивный опыт', 'EXCLUSIVE', 50000, 2, '/img/ceo.png', true);
INSERT INTO quests(name, description, type, target_count, reward_amount, active, target_audience) VALUES
('Проверить статус залога', 'Откройте раздел статуса залога', 'DAILY', 1, 10, true, 'ALL'),
('Прочитать статью о золоте', 'Ознакомьтесь со статьёй', 'DAILY', 1, 15, true, 'ALL'),
('Посмотреть калькулятор займа', 'Воспользуйтесь калькулятором', 'DAILY', 1, 10, true, 'ALL'),
 ('Пригласить друга', 'Поделитесь реферальной ссылкой', 'SEASONAL', 1, 200, true, 'ALL'),
('Новогодний квест', 'Выполните 5 заданий за декабрь', 'SEASONAL', 5, 500, true, 'ALL');
INSERT INTO leaderboard_entries(username, total_bonus_earned, league, month_year)
VALUES ('Игрок_A', 2500, 'GOLD', '2026-06'),
       ('Игрок_B', 1800, 'SILVER', '2026-06'),
       ('Игрок_C', 1200, 'BRONZE', '2026-06');
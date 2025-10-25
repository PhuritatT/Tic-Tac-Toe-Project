# Tic Tac Toe (XO)

**Features**
- เล่นกับผู้เล่นอีกคน (2 คน)
- เล่นกับ AI 3 ระดับ: Easy, Medium, Hard
- รองรับกระดานหลายขนาด (3×3, 4×4, 5×5 เป็นต้น)
- บันทึกประวัติและดู Replay เกมย้อนหลังได้

---

## Setup and Run

### Install Docker

#### For Windows

1. **Requirements**
   - Windows 10 64-bit (Pro/Enterprise/Education) or Windows 11
   - Enable Hyper-V and WSL 2

2. **Install WSL 2**
   ```powershell
   # Open PowerShell (Administrator)
   wsl --install
   # Restart your computer
   ```

3. **Install Docker Desktop**
   - Download from: https://www.docker.com/products/docker-desktop/
   - Run `Docker Desktop Installer.exe`
   - Follow the installation steps (use default settings)
   - Restart your computer

4. **Verify Installation**
   ```cmd
   docker --version
   docker compose version
   ```

#### For macOS

1. **Requirements**
   - macOS 10.15 (Catalina) or later
   - Check your chip (Intel or Apple Silicon M1/M2/M3)

2. **Install Docker Desktop**
   - Download from: https://www.docker.com/products/docker-desktop/
   - Choose the version for your chip
   - Open the `.dmg` file and drag Docker to Applications
   - Open Docker app and accept Terms

3. **Verify Installation**
   ```bash
   docker --version
   docker compose version
   ```

---

### Run the Project

#### Step 1: Navigate to Project
```bash
# Go to project folder
cd "Tic-Tac-Toe-Project"

# Make sure you have docker-compose.yml, backend/, frontend/
```

#### Step 2: Run Docker Compose
```bash
# First run (takes 5-10 minutes)
docker compose up --build

# Or run in background
docker compose up --build -d
```

#### Step 3: Access the Application
Open your web browser:
- **Game**: http://localhost:3000
- **API Backend**: http://localhost:8080
- **Database**: localhost:5432

#### Common Commands
```bash
# Check container status
docker compose ps

# View logs
docker compose logs -f

# Stop the project
docker compose down

# Rebuild after code changes
docker compose down
docker compose up --build
```

---

## Design

ในการออกแบบโปรแกรมเกม XO นี้ เราได้แบ่งขั้นตอนการออกแบบออกเป็น 3 ส่วนหลัก:

**ขั้นตอนการออกแบบ:**
1. **ออกแบบ Flow** - ออกแบบขั้นตอนการทำงานของระบบ ตั้งแต่เริ่มเกมใหม่, การเล่นเกม, ไปจนถึงการดูประวัติและ Replay
2. **ออกแบบ API** - ออกแบบ API endpoints ที่จำเป็นสำหรับรองรับการทำงานในแต่ละขั้นตอนของ Flow
3. **ออกแบบ Database** - ออกแบบโครงสร้าง Database ให้สอดคล้องกับการทำงานของ API ที่ออกแบบไว้

---

### 1. Flow

#### 1.1 New Game Flow
```
ผู้เล่นเข้าหน้าเว็บ 
→ เลือกขนาดกระดาน 
→ เลือกคู่แข่ง (Human/Bot) 
→ เลือกสัญลักษณ์ (X/O)
→ กด Start [API: POST /api/start]
→ Backend สร้าง session ใหม่ → บันทึกลง game_sessions table
→ ส่ง sessionId + board state กลับมา → แสดงกระดานเกม
```

#### 1.2 Gameplay Flow
```
ผู้เล่นคลิกช่อง [API: POST /api/games/move]
→ Backend validate (ช่องว่างหรือไม่, ตาของใคร)
→ อัปเดต game_sessions → บันทึก move ลง gameplay_records
→ เช็คชนะ/เสมอ (Win Detection Algorithm)
→ ถ้าชนะ/เสมอ: บันทึกผลลง result_boards
→ ถ้าเป็น Bot: คำนวณการเดิน Bot (Bot Algorithm)
→ ส่งสถานะเกมกลับ → แสดงผล
```

#### 1.3 View History Flow
```
ผู้เล่นกดดูประวัติ [API: GET /api/results]
→ ดึงข้อมูลจาก result_boards 
→ แสดงรายการเกมที่เล่นแล้วทั้งหมด (board size, opponent type, winner, total moves, completed date)
```

#### 1.4 Replay Flow
```
ผู้เล่นเลือกเกมที่ต้องการ Replay [API: GET /api/replays/{sessionId}]
→ ดึง moves ทั้งหมดจาก gameplay_records เรียงตาม move_number
→ แสดงแบบ step-by-step (Replay System)
→ ผู้เล่นสามารถใช้ปุ่ม Previous/Next เพื่อดูการเดินแต่ละตา
```

---

### 2. API Design

API endpoints ที่ออกแบบเพื่อรองรับการทำงานในแต่ละ Flow:

#### 2.1 Game Management APIs
- **POST /api/start** - สร้างเกมใหม่ (ใช้ใน New Game Flow)
- **POST /api/games/move** - บันทึกการเดินหมาก (ใช้ใน Gameplay Flow)
- **POST /api/surrender** - ยอมแพ้ (ใช้ใน Gameplay Flow)

#### 2.2 History & Replay APIs
- **GET /api/results** - ดึงรายการประวัติเกมทั้งหมด (ใช้ใน View History Flow)
- **GET /api/replays/{sessionId}** - ดึงข้อมูลการเดินทุกตาของเกม (ใช้ใน Replay Flow)

---

### 3. Database Schema

#### 3.1 game_sessions Table
**Purpose**: เก็บข้อมูล session ของเกม

**Key Fields**:
- `id` (BigInt, Primary Key): รหัส session ที่ไม่ซ้ำกัน (for internal use [table relation purpose])
- `session_id` (UUID, Primary Key): รหัส session ที่ไม่ซ้ำกัน (for external use [call from frontend])
- `board_size` (Integer): ขนาดกระดาน (3=3x3, 4=4x4, ...)
- `current_board_state` (Text): สถานะกระดานปัจจุบันเก็บเป็น String ("E,X,O,E,E...")
  - E = Empty (ช่องว่าง)
  - X = ผู้เล่น X
  - O = ผู้เล่น O
- `opponent_type` (String): ประเภทคู่แข่ง (HUMAN, BOT_EASY, BOT_MEDIUM, BOT_HARD)
- `game_status` (String): สถานะเกม
  - IN_PROGRESS = กำลังเล่น
  - X_WIN = X ชนะ
  - O_WIN = O ชนะ
  - DRAW = เสมอ
- `next_player` (String): ผู้เล่นตาถัดไป (X หรือ O)
- `human_player_symbol` (String): สัญลักษณ์ของผู้เล่นจริง (สำหรับเล่นกับ Bot)
- `created_at`, `updated_at` (Timestamp): เวลาสร้างและแก้ไข

**Used for**:
- สร้าง session ใหม่ตอนเริ่มเกม
- อัปเดตสถานะเกมทุกครั้งที่มีการเดิน

#### 3.2 gameplay_records Table
**Purpose**: เก็บข้อมูลการเล่นทุกตาของเกม

**Key Fields**:
- `id` (BigInt, Primary Key): รหัสอัตโนมัติ
- `game_session_id` (BigInt, Foreign Key): อ้างอิงไปยัง game_sessions column `id` 
- `move_number` (Integer): ลำดับการเดิน (1, 2, 3, ...)
- `row_index` (Integer): แถวที่เดิน (0, 1, 2, ...)
- `col_index` (Integer): คอลัมน์ที่เดิน (0, 1, 2, ...)
- `player` (String): ผู้เล่นที่เดิน (X หรือ O)
- `timestamp` (Timestamp): เวลาที่เดิน

**Used for**:
- บันทึกทุกตาที่เล่น (เก็บไว้สำหรับ Replay)
- ดึงข้อมูลการเดินทั้งหมดเมื่อต้องการ Replay

**Relationship**: 1 game_session มี gameplay_records หลายตัว (One-to-Many)

#### 3.3 result_boards Table
**Purpose**: เก็บข้อมูลผลการเล่นที่จบแล้ว

**Key Fields**:
- `id` (BigInt, Primary Key): running id
- `game_session_id` (BigInt, Foreign Key): ต่อกับ game_sessions เพื่อบอกว่าเป็นผลของ session ไหน
- `board_size` (Integer): ขนาดกระดาน
- `opponent_type` (String): ประเภทคู่แข่ง (HUMAN, BOT_EASY, BOT_MEDIUM, BOT_HARD)
- `winner` (String): ผู้ชนะ (X, O, DRAW, หรือ null)
- `total_moves` (Integer): จำนวนการเดินทั้งหมด
- `completed_at` (Timestamp): วันเวลาที่เล่นจบ

**Used for**:
- บันทึกผลลัพธ์เมื่อเกมจบ
- แสดงประวัติการเล่นทั้งหมดในหน้า Results
- Query และ filter ผลการเล่น

**Relationship**: 1 game_session has 1 result_board (One-to-One)

---

## Algorithm

### 1. Win Detection Algorithm

**Approach**: Check 4 directions

1. **แนวนอน (Horizontal)**: ตรวจทุกแถวว่ามี X หรือ O เรียงกันครบแถวหรือไม่ → วนลูปเช็คทีละช่องไปเรื่อยๆจนกว่าจะครบทุกแถว ถ้ามี X หรือ O เรียงกันครบแถว return true พร้อมกับบอกว่า X หรือ O ชนะ
2. **แนวตั้ง (Vertical)**: ตรวจทุกคอลัมน์ว่ามี X หรือ O เรียงกันครบคอลัมน์หรือไม่ → วนลูปเช็คทีละช่องไปเรื่อยๆจนกว่าจะครบทุกคอลัมน์ ถ้ามี X หรือ O เรียงกันครบคอลัมน์ return true พร้อมกับบอกว่า X หรือ O ชนะ
3. **เฉียงลง (Diagonal ↘)**: ตรวจเส้นทแยงจากมุมซ้ายบนไปมุมขวาล่าง → วนลูปเช็คทีละช่องไปเรื่อยๆจนกว่าจะครบทุกเส้นทแยง ถ้ามี X หรือ O เรียงกันครบเส้นทแยง return true พร้อมกับบอกว่า X หรือ O ชนะ
4. **เฉียงขึ้น (Anti-diagonal ↗)**: ตรวจเส้นทแยงจากมุมขวาบนไปมุมซ้ายล่าง → วนลูปเช็คทีละช่องไปเรื่อยๆจนกว่าจะครบทุกเส้นทแยง ถ้ามี X หรือ O เรียงกันครบเส้นทแยง return true พร้อมกับบอกว่า X หรือ O ชนะ
5. **เช็คเสมอ**: ถ้ากระดานเต็มแล้ว หรือ ไม่พบทางที่เป็นไปได้แล้วว่าจะชนะ = เสมอ

---

### 2. Bot AI Strategy

#### 2.1 Easy Bot
**Strategy**: Random move selection
1. **สุ่ม**: หาช่องว่างทั้งหมดในกระดาน → สุ่มเลือก 1 ช่อง

**Advantages**: Fast, suitable for beginners  

#### 2.2 Medium Bot
**Strategy**: Basic analysis
1. **ชนะก่อน**: ถ้ามีช่องที่เดินแล้วชนะได้ทันที → เดิน
2. **บล็อก**: ถ้าฝ่ายตรงข้ามจะชนะในตาถัดไป → บล็อก
3. **สุ่ม**: ถ้าไม่มีตัวเลือกข้างบน → สุ่มเลือก

**Advantages**: Decent gameplay, has strategy  

#### 2.3 Hard Bot
**Strategy**: More possibilities to win
1. **ชนะก่อน**: ถ้ามีช่องที่เดินแล้วชนะได้ทันที → เดิน
2. **บล็อก**: ถ้าฝ่ายตรงข้ามจะชนะในตาถัดไป → บล็อก
3. **เลือกกลาง**: ถ้าเป็นกระดานเลขคี่ (3x3, 5x5) ถ้าตรงกลางว่าง → เลือกกลางก่อน
4. **สุ่ม**: ถ้าไม่มีตัวเลือกข้างบน → สุ่มเลือก

**Advantages**: Stronger than Easy and Medium Bot

---

### 3. Replay System

**How it Works**:

1. **Data Recording**: 
   - ทุกครั้งที่มีการเดิน บันทึกลงตาราง `gameplay_records`
   - เก็บ `moveNumber`, `rowIndex`, `colIndex`, `player`

2. **Data Retrieval**:
   - เมื่อผู้เล่นเลือกเกมที่ต้องการ Replay
   - ดึง records ทั้งหมดเรียงตาม `moveNumber`

3. **Display**:
   - เริ่มจากกระดานเปล่า
   - มี index ชี้ว่ากำลังดูตาที่เท่าไหร่ (เริ่มจาก -1 = กระดานเปล่า)
   - วาดกระดานตั้งแต่ตาที่ 0 ถึง currentIndex
   - ปุ่ม Previous/Next เพื่อเปลี่ยน index

4. **Navigation**:
   - **Reset**: กลับไปตาที่ -1 (กระดานเปล่า)
   - **Previous**: ย้อนกลับ 1 ตา
   - **Next**: ไปหน้า 1 ตา

---

## Troubleshooting

### Port Already in Use
```bash
# macOS/Linux
lsof -i :3000
lsof -i :8080

# Windows
netstat -ano | findstr :3000
```

### Database Connection Failed
```bash
docker compose logs db
docker compose restart
```

### Build Failed
```bash
docker compose down
docker system prune -a
docker compose build --no-cache
docker compose up
```

---

## References

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Guide](https://spring.io/guides)
- [React Documentation](https://react.dev/)

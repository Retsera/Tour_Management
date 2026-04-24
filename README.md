# Tour_Management

Du an quan ly tour du lich full-stack gom backend Spring Boot va frontend React.

## Tong quan

- Backend: Spring Boot, Spring Security, JPA, WebSocket, JWT, MySQL, Ollama
- Frontend: React, TypeScript, Vite, Tailwind, Zustand
- Tich hop: dang nhap Google OAuth2, chat/AI, dat tour, thanh toan, thong bao realtime

## Cau truc

```text
Tour_Management/
  Backend/
  Frontend/
  README.md
  .gitignore
```

## Yeu cau moi truong

- Java 21
- Node.js 18+ (khuyen nghi 20+)
- npm
- MySQL 8+
- Ollama neu muon dung AI chat/embedding

## Cau hinh backend

File cau hinh chinh: [Backend/src/main/resources/application.properties](Backend/src/main/resources/application.properties)

Can kiem tra cac thong so sau truoc khi chay:

- `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`
- `spring.ai.ollama.base-url`
- `mt.jwt.base64-secret`
- `mt.upload-file.base-uri`

Mac dinh backend dang su dung:

- MySQL tai `jdbc:mysql://localhost:3306/TourManagement`
- Ollama tai `http://localhost:11434`
- API goc tai `http://localhost:8080/api/v1`

Neu thay doi host hoac port, hay cap nhat frontend theo cung gia tri.

## Cau hinh frontend

Frontend dang lay base URL qua bien moi truong `BASE_API_URL`.

Tao file `Frontend/.env` neu can doi backend:

```env
BASE_API_URL=http://localhost:8080/api/v1
```

Frontend hien co tham chieu truc tiep den backend o `http://localhost:8080` cho OAuth2 va WebSocket, vi vay backend nen chay tren port nay neu khong doi code.

## Chay du an

### Backend

Windows:

```bash
cd Backend
gradlew.bat bootRun
```

macOS/Linux:

```bash
cd Backend
./gradlew bootRun
```

### Frontend

```bash
cd Frontend
npm install
npm run dev
```

## Build san pham

### Backend

```bash
cd Backend
gradlew.bat build
```

### Frontend

```bash
cd Frontend
npm run build
```

## Ghi chu

- Repo goc da duoc khoi tao Git va push len GitHub.
- Neu ban clone ve may moi, hay tao database `TourManagement` truoc khi chay backend.
- Cac file build va thu muc phat sinh da duoc bo sung vao `.gitignore` o root repo.

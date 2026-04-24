# Tour_Management

Du an quan ly tour du lich full-stack, gom:

- `Backend`: Spring Boot, Spring Security, JPA, MySQL, WebSocket, JWT, AI/Ollama
- `Frontend`: React + TypeScript + Vite

## Cau truc

```text
Tour_Management/
  Backend/
  Frontend/
```

## Yeu cau

- Java 21
- Node.js 18+ (khuyen nghi 20+)
- MySQL 8+
- Ollama neu muon dung tinh nang AI chat/embedding

## Chay backend

```bash
cd Backend
./gradlew bootRun
```

Neu dung Windows:

```bash
cd Backend
gradlew.bat bootRun
```

Backend mac dinh cau hinh trong `Backend/src/main/resources/application.properties`.

## Chay frontend

```bash
cd Frontend
npm install
npm run dev
```

## Build frontend

```bash
cd Frontend
npm run build
```

## Ghi chu

- Cac thong so database, JWT, va Ollama hien dang duoc cau hinh trong `Backend/src/main/resources/application.properties`.
- Neu can public len GitHub/GitLab, hay them remote va push repo sau khi khoi tao Git.

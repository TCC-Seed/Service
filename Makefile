# Executa o Maven dentro do container "app" já em execução
MVN := docker compose exec app mvn

.PHONY: up run down clean restart \
        mvn-version build test format-check format checkstyle pmd spotbugs lint

# ---------- Docker ----------
up:
	docker compose up

run:
	docker compose up --build

down:
	docker compose down

restart: down up

clean:
	docker compose down -v

# ---------- Build e testes (dentro do container) ----------
mvn-version:
	$(MVN) -v

build:
	$(MVN) clean compile

test:
	$(MVN) test

# ---------- Qualidade de código (dentro do container) ----------
format:
	$(MVN) spotless:apply

lint:
	-$(MVN) spotless:check
	-$(MVN) checkstyle:check
	-$(MVN) pmd:check
	-$(MVN) compile spotbugs:check
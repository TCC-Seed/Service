.PHONY: up run down build logs clean restart ps

up:
	docker compose up

run:
	docker compose up --build

down:
	docker compose down

restart: down up

clean:
	docker compose down -v
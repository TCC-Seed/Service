.PHONY: up run down clean restart

up:
	docker compose up

run:
	docker compose up --build

down:
	docker compose down

restart: down up

clean:
	docker compose down -v
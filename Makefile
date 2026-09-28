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

# npx skills add mattpocock/skills --skill grilling --skill domain-modeling --skill grill-with-docs --agent codex
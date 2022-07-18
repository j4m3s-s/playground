FROM python:3.10-bullseye AS build
ENV PYTHONUNBUFFERED=true
WORKDIR /var/back
RUN mkdir -p /var/back/

FROM build AS poetry
RUN curl -sSL https://raw.githubusercontent.com/python-poetry/poetry/master/get-poetry.py | python -

COPY pyproject.toml .
COPY poetry.lock .
ENV POETRY_VIRTUALENVS_IN_PROJECT=true
ENV PATH="$POETRY_HOME/bin:$PATH"
RUN $HOME/.poetry/bin/poetry install --no-interaction --no-ansi -vvv

FROM build
ENV PATH="/var/back/.venv/bin:$PATH"
COPY --from=poetry /var/back .
COPY . .
RUN ./manage.py collectstatic

USER 1000
ENTRYPOINT [ "/var/back/entrypoint.sh" ]

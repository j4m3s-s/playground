#!/usr/bin/env sh
back_version="$1"
front_version="$2"

function build_push_img () {
    software="$1"
    version="$2"
    img="j4m3s/flashcard-$software:$version"

    (
    cd "$software"
    sudo docker build . -t "$img"
    sudo docker push "$img"
    )
}

build_push_img back "$back_version"
build_push_img front "$front_version"

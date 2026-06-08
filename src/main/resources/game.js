let towerGameInstance = null;

class TowerBlocksGame {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');
        this.running = false;
        this.animId = null;
        this.reset();
    }

    reset() {
        this.blocks = [];
        this.currentBlock = null;
        this.speed = 3;
        this.direction = 1;
        this.score = 0;
        this.cameraY = 0;
        this.blockHeight = 30;
        this.running = true;

        const base = {
            x: this.canvas.width / 2 - 80,
            y: this.canvas.height - 40,
            w: 160,
            h: 40,
            color: '#e63946'
        };
        this.blocks.push(base);
        this.spawnBlock();
        this.updateScoreDisplay();
    }

    spawnBlock() {
        const last = this.blocks[this.blocks.length - 1];
        this.currentBlock = {
            x: this.direction > 0 ? -last.w : this.canvas.width,
            y: last.y - this.blockHeight,
            w: last.w,
            h: this.blockHeight,
            color: `hsl(${(this.score * 10 + 350) % 360}, 70%, 50%)`
        };
    }

    place() {
        if (!this.running || !this.currentBlock) return;
        const last = this.blocks[this.blocks.length - 1];
        const curr = this.currentBlock;

        const overlapStart = Math.max(curr.x, last.x);
        const overlapEnd = Math.min(curr.x + curr.w, last.x + last.w);
        const overlap = overlapEnd - overlapStart;

        if (overlap <= 0) {
            this.running = false;
            showToast('error', 'Башня упала!', `Счёт: ${this.score}`, '💥');
            setTimeout(() => this.drawGameOver(), 100);
            return;
        }

        const isPerfect = Math.abs(curr.x - last.x) < 5;
        if (isPerfect) {
            curr.x = last.x;
            curr.w = last.w;
            showToast('success', 'PERFECT!', 'Идеальное попадание', '🎯');
        } else {
            curr.w = overlap;
            if (curr.x < last.x) curr.x = last.x;
        }

        this.blocks.push({ ...curr });
        this.score += 10 + (isPerfect ? 5 : 0);
        this.updateScoreDisplay();

        if (this.blocks.length > 3) {
            this.cameraY -= this.blockHeight;
        }

        this.speed = Math.max(1, 3 + (this.blocks.length - 1) * 0.15);
        this.spawnBlock();
    }

    updateScoreDisplay() {
        const scoreEl = document.getElementById('gameScore');
        if (scoreEl) scoreEl.textContent = `Счёт: ${this.score}`;
    }

    update() {
        if (!this.currentBlock || !this.running) return;
        this.currentBlock.x += this.speed * this.direction;
        if (this.currentBlock.x <= 0) this.direction = 1;
        if (this.currentBlock.x + this.currentBlock.w >= this.canvas.width) this.direction = -1;
    }

    drawBlock(block, cameraOffset) {
        const y = block.y + cameraOffset;
        this.ctx.fillStyle = block.color;
        this.ctx.fillRect(block.x, y, block.w, block.h);
        this.ctx.fillStyle = 'rgba(255,255,255,0.2)';
        this.ctx.fillRect(block.x, y, block.w, 3);
        this.ctx.strokeStyle = 'rgba(0,0,0,0.4)';
        this.ctx.lineWidth = 1;
        this.ctx.strokeRect(block.x, y, block.w, block.h);
    }

    draw() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        const grad = this.ctx.createLinearGradient(0, 0, 0, this.canvas.height);
        grad.addColorStop(0, '#1a0000');
        grad.addColorStop(1, '#0a0505');
        this.ctx.fillStyle = grad;
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

        const cameraOffset = -this.cameraY + this.canvas.height - this.blocks[0].y - this.blocks[0].h - 50;

        this.blocks.forEach(b => this.drawBlock(b, cameraOffset));
        if (this.currentBlock) this.drawBlock(this.currentBlock, cameraOffset);
    }

    drawGameOver() {
        this.ctx.fillStyle = 'rgba(0,0,0,0.75)';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.fillStyle = '#ff1744';
        this.ctx.font = 'bold 40px Orbitron';
        this.ctx.textAlign = 'center';
        this.ctx.fillText('GAME OVER', this.canvas.width / 2, this.canvas.height / 2);
    }

    loop() {
        if (!this.running) {
            this.draw();
            return;
        }
        this.update();
        this.draw();
        this.animId = requestAnimationFrame(() => this.loop());
    }

    start() {
        if (this.animId) cancelAnimationFrame(this.animId);
        this.reset();
        this.loop();
    }

    stop() {
        this.running = false;
        if (this.animId) cancelAnimationFrame(this.animId);
    }
}

function initTowerBlocks() {
    if (towerGameInstance) return;
    towerGameInstance = new TowerBlocksGame('towerCanvas');

    document.getElementById('btnStartGame').addEventListener('click', () => {
        towerGameInstance.start();
    });

    document.getElementById('btnPlaceBlock').addEventListener('click', () => {
        towerGameInstance.place();
    });

    window.addEventListener('keydown', (e) => {
        if (e.code === 'Space' && document.getElementById('game').classList.contains('active')) {
            e.preventDefault();
            towerGameInstance.place();
        }
    });

    document.getElementById('towerCanvas').addEventListener('click', () => {
        towerGameInstance.place();
    });

    towerGameInstance.draw();
}
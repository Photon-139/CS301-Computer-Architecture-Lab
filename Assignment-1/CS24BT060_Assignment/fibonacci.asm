	.data
n:
	10
	.text
main:
    load $x0, $n, %x3
    addi %x0, 65535, %x4
    add %x0, %x0, %x5
    store %x5, 0, %x4
    subi %x4, 1, %x4
    addi %x5, 1, %x5
    store %x5, 0, %x4
    subi %x4, 1, %x4
    subi %x3, 1, %x3
    jmp loop
loop:
    beq %x3, %x0, endl
    addi %x4, 1, %x6
    load %x6, 0, %x7
    addi %x6, 1, %x6
    load %x6, 0, %x8
    add %x7, %x8, %x5
    store %x5, 0, %x4
    subi %x3, 1, %x3
    subi %x4, 1, %x4
    jmp loop
endl:
    end
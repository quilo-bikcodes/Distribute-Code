def vcounter(x):
	vowel_counter = 0
	for i in x:
		if i in "aeiou":
			vowel_counter += 1
	return vowel_counter


user_input = input("Enter Somthing: ")
# result = vcounter(user_input)
print(vcounter(user_input))

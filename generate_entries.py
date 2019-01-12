import argparse

parser = argparse.ArgumentParser(description='Generate entries for the given mob and put the files in the correct place')
parser.add_argument('registry', help='Registry name of the entity, eg minecraft:zombie')
parser.add_argument('display', help='Display name of the entity, eg Zombie')
parser.add_argument('category', help='Category of the entity, must be a (animals), h (helpers), m (monsters) or b (bosses)')

args = parser.parse_args()

registry = args.registry
display = args.display
category = args.category

if category == 'a' or category == 'A' or category == 'animals':
	category = 'animals'
elif category == 'h' or category == 'H' or category == 'helpers':
	category = 'helpers'
elif category == 'm' or category == 'M' or category == 'monsters':
	category = 'monsters'
elif category == 'b' or category == 'B' or category == 'bosses':
	category = 'bosses'
else:
	raise ValueError('Category argument was invalid. Must be a (animals), h (helpers), m (monsters) or b (bosses).')

print('Reading templates')

with open('./templates/advancement.json', 'r') as file_adv:
	advancement = file_adv.read()
with open('./templates/entry.json', 'r') as file_ent:
	entry = file_ent.read()

filename = registry.replace(':', '_', 1)

print('Filling fields')

advancement = advancement.replace('@MOB@', registry)
entry = entry.replace('@MOB@', registry).replace('@NAME@', display).replace('@CATEGORY@', category).replace('@FILE@', filename)

print('Writing files')

with open('./src/main/resources/assets/universalbestiary/advancements/{}.json'.format(filename), 'w') as file_adv:
	file_adv.write(advancement)
with open('./src/main/resources/assets/universalbestiary/patchouli_books/bestiary/en_us/entries/{}.json'.format(filename), 'w') as file_ent:
	file_ent.write(entry)

print('Done!')